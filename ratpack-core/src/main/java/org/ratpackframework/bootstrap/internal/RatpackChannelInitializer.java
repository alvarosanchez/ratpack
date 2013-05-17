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

package org.ratpackframework.bootstrap.internal;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.ratpackframework.handling.Handler;

import java.io.File;

public class RatpackChannelInitializer extends ChannelInitializer<SocketChannel> {

  private final int workerThreads;
  private final Handler handler;
  private final File baseDir;

  public RatpackChannelInitializer(int workerThreads, Handler handler, File baseDir) {
    this.workerThreads = workerThreads;
    this.handler = handler;
    this.baseDir = baseDir;
  }



  public void initChannel(SocketChannel ch) throws Exception {
    // Create a default pipeline implementation.
    ChannelPipeline pipeline = ch.pipeline();

    pipeline.addLast("decoder", new HttpRequestDecoder());
    pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
    pipeline.addLast("encoder", new HttpResponseEncoder());
    pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

    NettyRoutingAdapter nettyRoutingAdapter = new NettyRoutingAdapter(handler, baseDir);

    if (workerThreads > 0) {
      pipeline.addLast(new DefaultEventExecutorGroup(workerThreads), "handler", nettyRoutingAdapter);
    } else {
      pipeline.addLast("handler", nettyRoutingAdapter);
    }
  }
}