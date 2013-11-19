package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.Task

object Application extends Controller {
  /**
   * Generic homepage.
   * @return
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  /**
   * Validates and returns the Task provided, or displays errors.
   * @return
   */
  def postTask = Action(parse.json) { request =>
    request.body.validate[Task].map{
      case task => Ok(Json.toJson(task))
    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }
}