package com.github.mich8bsp

import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.http.filter.Cors.HttpFilter
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter

class Server extends HttpServer{
  override def defaultHttpPort: String = s":${
    val port = System.getenv("PORT")
    if(port!=null) port else "9992"
  }"

  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter(new HttpFilter(Cors.UnsafePermissivePolicy))
      .add[GameController]
  }
}


object ServerMain extends Server