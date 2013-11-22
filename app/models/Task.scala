package models

import org.joda.time.DateTime
import reactivemongo.bson._

/**
 * Models a Task.
 * @param id The unique identifier for this task.
 * @param user The user the task belongs to.
 * @param description A description of the task.
 * @param dueDate An optional date when the task must be completed by.
 */
case class Task (id: Option[BSONObjectID],
                 user: String,
                 description: String,
                 dueDate: Option[DateTime])

/**
 * Companion object to support JSON transformations.
 */
object Task {
  // import just JSON Reads/Writes helpers in scope
  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val jsonReads: Reads[Task] = (
    (__ \ "id").readNullable[BSONObjectID].map(_.getOrElse(BSONObjectID.generate)).map(Some(_)) and
    (__ \ "user").read[String] and
    (__ \ "description").read[String] and
    (__ \ "dueDate").readNullable[DateTime]
  )(Task.apply _)

  implicit val jsonWrites: Writes[Task] = (
    (__ \ "id").writeNullable[BSONObjectID] and
    (__ \ "user").write[String] and
    (__ \ "description").write[String] and
    (__ \ "dueDate").writeNullable[DateTime]
  )(unlift(Task.unapply))
}