package controllers

import play.api.mvc._
import akka.actor.Props
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration.Duration
import play.api.libs.concurrent.Execution.Implicits._
import actors._


import play.api.libs.json._
import play.api.data._
import play.api.data.Forms._
import scala.collection.mutable.ArrayBuffer
import actors.Event
import actors.LogItem

object Application extends Controller {

  implicit val timeout: Timeout = Timeout(Duration(3, "seconds"))

  implicit val eventFormat = Json.format[Event]

  private val analyticsLogger = Akka.system.actorOf(Props[AnalyticsLogger], name = "analyticsLogger")

  def addLog = Action { implicit request =>

    val inputForm = Form(
      mapping(
        "deviceId" -> text,
        "x" -> number,
        "y" -> number,
        "time" -> ignored(System.currentTimeMillis / 1000)
      )(LogItem.apply)(LogItem.unapply)
    )

    inputForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      data => {
        analyticsLogger ! data
        Ok(Json.obj("success" -> true))
      }
    )
  }

  def getEvents(timestamp: Long) = Action.async {
    (analyticsLogger ? EventsListAfterMsg(timestamp)) map {
      case e: ArrayBuffer[Event] => {
        // TODO may be rewrite test output?
        Ok(Json.arr(e))
      }
      case _ => BadRequest
    }
  }
}