/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.server.internal;


import ratpack.handling.Context;
import ratpack.server.BindAddress;
import ratpack.server.PublicAddress;
import ratpack.util.ExceptionUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class DefaultPublicAddress implements PublicAddress {

  private final URI publicAddress;
  private final String uriScheme;

  public DefaultPublicAddress(URI publicAddress, String uriScheme) {
    this.publicAddress = publicAddress;
    this.uriScheme = uriScheme;
  }

  public URI getAddress(Context context) {
    URI currentUrl;
    if (publicAddress == null) {
      BindAddress bindAddress = context.getBindAddress();
      try {
        currentUrl = new URI(uriScheme, null, bindAddress.getHost(), bindAddress.getPort(), null, null, null);
      } catch (URISyntaxException e) {
        throw ExceptionUtils.uncheck(e);
      }
    } else {
      currentUrl = publicAddress;
    }
    return currentUrl;
  }
}