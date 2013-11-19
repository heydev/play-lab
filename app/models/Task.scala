package models

import org.joda.time.DateTime

/**
 * Models a Task.
 * @param id The unique id of the object.
 * @param user The user the task belongs to.
 * @param description A description of the task.
 * @param dueDate An optional date when the task must be completed by.
 */
case class Task (id: String,
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

  implicit val reads: Reads[Task] = (
    (__ \ "id").read[String] and
    (__ \ "user").read[String] and
    (__ \ "description").read[String] and
    (__ \ "dueDate").readNullable[DateTime]
  )(Task.apply _)

  implicit val writes: Writes[Task] = (
    (__ \ "id").write[String] and
    (__ \ "user").write[String] and
    (__ \ "description").write[String] and
    (__ \ "dueDate").writeNullable[DateTime]
  )(unlift(Task.unapply))
}