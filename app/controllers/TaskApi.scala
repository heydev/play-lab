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

  def collection = db.collection[JSONCollection]("tasks")

  def postTask = Action.async(parse.json) { request =>
    request.body.validate[Task].map {
      case task => {
        val futureResult = collection.insert(task)
        futureResult.map {
          case t => t.inError match {
            case true => InternalServerError("%s".format(t))
            case false => Ok(Json.toJson(task))
          }
        }
      }
    }.recoverTotal{
      e => Future { BadRequest(JsError.toFlatJson(e)) }
    }
  }

  def getTasks(user: String) = Action.async(parse.anyContent) { request =>
    val cursor = collection.find(Json.obj("user" -> user)).cursor[Task]
    val futureResults: Future[List[Task]] = cursor.collect[List]()
    futureResults.map {
      case t => Ok(Json.toJson(t))
    }
  }
}
