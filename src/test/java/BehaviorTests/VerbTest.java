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

import unirest.HttpMethod;
import unirest.HttpResponse;
import unirest.Unirest;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class VerbTest extends BddTest {
    @Test
    public void get() {
        Unirest.get(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.GET);
    }

    @Test
    public void post() {
        Unirest.post(MockServer.POST)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.POST);
    }

    @Test
    public void put() {
        Unirest.put(MockServer.POST)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.PUT);
    }

    @Test
    public void patch() {
        Unirest.patch(MockServer.PATCH)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.PATCH);
    }

    @Test
    public void head() {
        HttpResponse<InputStream> response = Unirest.head(MockServer.GET).asBinary();

        assertEquals(200, response.getStatus());
        assertEquals("text/html;charset=utf-8", response.getHeaders().getFirst("Content-Type"));
    }

    @Test
    public void option() {
        Unirest.options(MockServer.GET)
                .asObject(RequestCapture.class)
                .getBody()
                .asserMethod(HttpMethod.OPTIONS);
    }
}
