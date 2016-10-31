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
 * Manage projects related operations.
 */
class Projects @Inject() (val projectService: ProjectService,
                          val taskService: TaskService,
                          val userService: UserService) extends Controller with Secured {

  /**
   * Display the dashboard.
   */
  def index = IsAuthenticated { username => _ =>
    userService.findByEmail(username).map { user =>
      Ok(
        html.dashboard(
          projectService.findInvolving(username), 
          taskService.findTodoInvolving(username), 
          user
        )
      )
    }.getOrElse(Forbidden)
  }

  // -- Projects

  /**
   * Add a project.
   */
  def add = IsAuthenticated { username => implicit request =>
    Form("group" -> nonEmptyText).bindFromRequest.fold(
      errors => BadRequest,
      folder => Ok(
        views.html.projects.item(
          projectService.create(
            Project(None, folder, "New project"), 
            Seq(username)
          )
        )
      )
    )
  }

  /**
   * Delete a project.
   */
  def delete(project: Long) = IsMemberOf(project) { username => _ =>
    projectService.delete(project)
    Ok
  }

  /**
   * Rename a project.
   */
  def rename(project: Long) = IsMemberOf(project) { _ => implicit request =>
    Form("name" -> nonEmptyText).bindFromRequest.fold(
      errors => BadRequest,
      newName => { 
        projectService.rename(project, newName) 
        Ok(newName) 
      }
    )
  }

  // -- Project groups

  /**
   * Add a new project group.
   */
  def addGroup = IsAuthenticated { _ => _ =>
    Ok(html.projects.group("New group"))
  }

  /**
   * Delete a project group.
   */
  def deleteGroup(folder: String) = IsAuthenticated { _ => _ =>
    projectService.deleteInFolder(folder)
    Ok
  }

  /**
   * Rename a project group.
   */
  def renameGroup(folder: String) = IsAuthenticated { _ => implicit request =>
    Form("name" -> nonEmptyText).bindFromRequest.fold(
      errors => BadRequest,
      newName => { projectService.renameFolder(folder, newName); Ok(newName) }
    )
  }

  // -- Members

  /**
   * Add a project member.
   */
  def addUser(project: Long) = IsMemberOf(project) { _ => implicit request =>
    Form("user" -> nonEmptyText).bindFromRequest.fold(
      errors => BadRequest,
      user => { projectService.addMember(project, user); Ok }
    )
  }

  /**
   * Remove a project member.
   */
  def removeUser(project: Long) = IsMemberOf(project) { _ => implicit request =>
    Form("user" -> nonEmptyText).bindFromRequest.fold(
      errors => BadRequest,
      user => { projectService.removeMember(project, user); Ok }
    )
  }

}

