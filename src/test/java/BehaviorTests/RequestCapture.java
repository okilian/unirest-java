/**
 * The MIT License
 *
 * Copyright for portions of OpenUnirest/uniresr-java are held by Mashape (c) 2013 as part of Kong/unirest-java.
 * All other copyright for OpenUnirest/unirest-java are held by OpenUnirest (c) 2018.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package BehaviorTests;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import unirest.JsonPatch;
import unirest.JsonPatchItem;
import unirest.JsonPatchOperation;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import spark.Request;
import unirest.HttpMethod;
import unirest.TestUtil;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static unirest.JsonPatchRequest.CONTENT_TYPE;
import static java.lang.System.getProperty;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class RequestCapture {
    public Map<String, String> headers = new LinkedHashMap<>();
    public List<File> files = new ArrayList<>();
    public Multimap<String, String> params = HashMultimap.create();
    public String body;
    public String url;
    public String queryString;
    public HttpMethod method;
    public String param;
    public String contentType;
    public JsonPatch jsonPatches;
    public Integer status;
    private boolean isProxied;


    public RequestCapture() {
    }

    public RequestCapture(Request req) {
        url = req.url();
        queryString = req.queryString();
        method = HttpMethod.valueOf(req.requestMethod());
        writeHeaders(req);
        writeQuery(req);
        param = req.params("p");
        contentType = req.contentType();
        status = 200;
    }

    public void writeBody(Request req) {
        if(Strings.nullToEmpty(req.contentType()).equals(CONTENT_TYPE)){
            String body = req.body();
            jsonPatches = new JsonPatch(body);
            this.body = jsonPatches.toString();
        } else {
            //parseBodyToFormParams(req);
            writeMultipart(req);
        }
    }

    private void parseBodyToFormParams() {
        URLEncodedUtils.parse(this.body , Charset.forName("UTF-8"))
                .forEach(p -> {
                    params.put(p.getName(), p.getValue());
                });
    }

    public void writeMultipart(Request req) {
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(getProperty("java.io.tmpdir")));

        try {
            for (Part p : req.raw().getParts()) {
                if (!Strings.isNullOrEmpty(p.getSubmittedFileName())) {
                    buildFilePart(p);
                } else {
                    buildFormPart(p);
                }
            }
        } catch (ServletException e){
            this.body = req.body();
            parseBodyToFormParams();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void buildFormPart(Part p) throws IOException {
        java.util.Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\A");
        String value = s.hasNext() ? s.next() : "";
        params.put(p.getName(), value);
    }

    public void buildFilePart(Part part) throws IOException {
        File file = new File();
        file.fileName = part.getSubmittedFileName();
        file.type = part.getContentType();
        file.inputName = part.getName();
        file.fileType = part.getContentType();
        file.body = TestUtil.toString(part.getInputStream());

        files.add(file);
    }

    private void writeQuery(Request req) {
        req.queryParams().forEach(q -> params.putAll(q, Sets.newHashSet(req.queryMap(q).values())));
    }

    public RequestCapture asserBody(String s) {
        assertEquals(s, body);
        return this;
    }

    public RequestCapture assertNoHeader(String s) {
        assertFalse("Should Have No Header " + s, headers.containsKey(s));
        return this;
    }

    private RequestCapture writeHeaders(Request req) {
        req.headers().forEach(h -> headers.put(h, req.headers(h)));
        return this;
    }

    public RequestCapture assertHeader(String key, String value) {
        assertEquals("Expected Header Failed", value, headers.get(key));
        return this;
    }

    public RequestCapture assertParam(String key, String value) {
        assertThat("Expected Query or Form value", params.get(key), hasItem(value));
        return this;
    }

    public File getFile(String fileName) {
        return files.stream()
                .filter(f -> Objects.equals(f.fileName, fileName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("\nNo File With Name: " + fileName + "\n"
                        + "Found: " + files.stream().map(f -> f.fileName).collect(Collectors.joining(" "))));
    }

    public File getFileByInput(String input) {
        return files.stream()
                .filter(f -> Objects.equals(f.inputName, input))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No File from form: " + input));
    }

    public RequestCapture assertFileContent(String input, String content) {
        assertEquals(content, getFileByInput(input).body);
        return this;
    }

    public RequestCapture assertBasicAuth(String username, String password) {
        String raw = headers.get("Authorization");
        TestUtil.assertBasicAuth(raw, username, password);
        return this;
    }

    public RequestCapture assertQueryString(String s) {
        assertEquals(s, queryString);
        return this;
    }

    public RequestCapture asserMethod(HttpMethod get) {
        assertEquals(get, method);
        return this;
    }

    public RequestCapture assertPathParam(String value) {
        assertEquals(value, param);
        return this;
    }

    public RequestCapture assertUrl(String s) {
        assertEquals(s, url);
        return this;
    }

    public void assertCharset(Charset charset) {
        assertThat(contentType, endsWith(charset.toString()));
    }

    public RequestCapture assertJsonPatch(JsonPatchOperation op, String path, Object value) {
        assertNotNull("Asserting JSONPatch but no patch object present", jsonPatches);
        assertThat(jsonPatches.getOperations(), hasItem(new JsonPatchItem(op, path, value)));
        return this;
    }

    public void setPatch(JsonPatch patch) {
        this.jsonPatches = patch;
    }

    public RequestCapture assertStatus(Integer i) {
         assertEquals(i, status);
         return this;
    }

    public void setIsProxied(boolean b) {
        this.isProxied = b;
    }

    public RequestCapture assertIsProxied(boolean b) {
        assertEquals(b, isProxied);
        return this;
    }

    public static class File {
        public String fileName;
        public String type;
        public String inputName;
        public String body;
        public String fileType;

        public File assertBody(String content){
            assertEquals(content, body);
            return this;
        }

        public File assertFileType(String type){
            assertEquals(type, this.fileType);
            return this;
        }

        public File assertFileType(ContentType imageJpeg) {
            return assertFileType(imageJpeg.toString());
        }

        public File assertFileName(String s) {
            assertEquals(s, fileName);
            return this;
        }
    }
}
