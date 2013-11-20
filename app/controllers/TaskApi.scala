package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import models.Task

// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object TaskApi extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("tasks")

  /**
   * Validates and returns the Task provided, or displays errors.
   * @return
   */
  def postTask = Action(parse.json) { request =>
    request.body.validate[Task].map{
      case task => {
        val futureResult = collection.insert(task)
        Async {
          // when the insert is performed, send a OK 200 result
          futureResult.map(_ => Ok)
        }
      }
    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }
}
