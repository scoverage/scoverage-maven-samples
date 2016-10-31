package controllers

import javax.inject.Inject

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.db._
import play.api.mvc._

import models._
import views._

/**
 * Manage tasks related operations.
 */
class Tasks @Inject() (val projectService: ProjectService,
                       val taskService: TaskService,
                       val userService: UserService) extends Controller with Secured {

  /**
   * Display the tasks panel for this project.
   */
  def index(project: Long) = IsMemberOf(project) { _ => implicit request =>
    projectService.findById(project).map { p =>
      val tasks = taskService.findByProject(project)
      val team = projectService.membersOf(project)
      Ok(html.tasks.index(p, tasks, team, userService))
    }.getOrElse(NotFound)
  }

  val taskForm = Form(
    tuple(
      "title" -> nonEmptyText,
      "dueDate" -> optional(date("MM/dd/yy")),
      "assignedTo" -> optional(text)
    )
  )

  // -- Tasks

  /**
   * Create a task in this project.
   */  
  def add(project: Long, folder: String) =  IsMemberOf(project) { _ => implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest,
      {
        case (title, dueDate, assignedTo) => 
          val task = taskService.create(
            Task(None, folder, project, title, false, dueDate, assignedTo)
          )
          Ok(html.tasks.item(task))
      }
    )
  }

  /**
   * Update a task
   */
  def update(task: Long) = IsOwnerOf(task) { _ => implicit request =>
    Form("done" -> boolean).bindFromRequest.fold(
      errors => BadRequest,
      isDone => { 
        taskService.markAsDone(task, isDone)
        Ok 
      }
    )
  }

  /**
   * Delete a task
   */
  def delete(task: Long) = IsOwnerOf(task) { _ => implicit request =>
    taskService.delete(task)
    Ok
  }

  // -- Task folders

  /**
   * Add a new folder.
   */
  def addFolder = Action {
    Ok(html.tasks.folder("New folder"))
  }

  /**
   * Delete a full tasks folder.
   */
  def deleteFolder(project: Long, folder: String) = IsMemberOf(project) { _ => implicit request =>
    taskService.deleteInFolder(project, folder)
    Ok
  }

  /**
   * Rename a tasks folder.
   */
  def renameFolder(project: Long, folder: String) = IsMemberOf(project) { _ => implicit request =>
    Form("name" -> nonEmptyText).bindFromRequest.fold(
      errors => BadRequest,
      newName => { 
        taskService.renameFolder(project, folder, newName) 
        Ok(newName) 
      }
    )
  }

}

