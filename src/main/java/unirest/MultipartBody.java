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

package unirest;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;

public interface MultipartBody extends HttpRequest<MultipartBody>, Body {
    MultipartBody field(String name, String value);

    MultipartBody field(String name, String value, String contentType);

    MultipartBody field(String name, Collection<?> collection);

    MultipartBody field(String name, File file);

    MultipartBody field(String name, File file, String contentType);

    @Deprecated // In version 4 Apache classes will be abstracted out
    MultipartBody field(String name, InputStream value, ContentType contentType);

    @Deprecated // In version 4 Apache classes will be abstracted out
    MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName);

    @Deprecated // In version 4 Apache classes will be abstracted out
    MultipartBody field(String name, byte[] bytes, ContentType contentType, String fileName);

    MultipartBody field(String name, InputStream stream, String fileName);

    MultipartBody field(String name, byte[] bytes, String fileName);

    MultipartBody charset(Charset charset);

    MultipartBody contentType(String mimeType);

    MultipartBody mode(String value);

    MultipartBody mode(HttpMultipartMode value);
}
