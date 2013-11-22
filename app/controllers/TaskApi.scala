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

/**
 * Our TaskApi controller which utilizes ReactiveMongo
 * for persistence and retrieval.
 */
object TaskApi extends Controller with MongoController {
  /**
   * Our JSON MongoDB collection. It does the BSON translation
   * for us, letting our Play app deal strictly in JSON.
   * We're using a def instead of a val to prevent Play Framework
   * hot loading.
   * @return
   */
  def collection = db.collection[JSONCollection]("tasks")

  /**
   * For posting new Tasks.
   * @return A persisted Task entity, or an error.
   */
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

  /**
   * For getting Task entities by user.
   * @param user The user to filter with
   * @return A list of Tasks for the given user.
   */
  def getTasks(user: String) = Action.async(parse.anyContent) { request =>
    val cursor = collection.find(Json.obj("user" -> user)).cursor[Task]
    val futureResults = cursor.collect[List]()
    futureResults.map {
      case t => Ok(Json.toJson(t))
    }
  }
}
