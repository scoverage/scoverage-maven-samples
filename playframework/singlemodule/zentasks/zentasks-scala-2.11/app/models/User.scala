package models

import java.sql.Connection

import javax.inject.Inject
import javax.inject.Singleton

import play.api.db._

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class User(email: String, name: String, password: String)

@Singleton
class UserService @Inject() (dbapi: DBApi) {
  
  private val db = dbapi.database("default")

  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.email") ~
    get[String]("user.name") ~
    get[String]("user.password") map {
      case email~name~password => User(email, name, password)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    db.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        "email" -> email
      ).as(simple.singleOpt)
    }
  }
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    db.withConnection { implicit connection =>
      SQL("select * from user").as(simple *)
    }
  }
  
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    db.withConnection { implicit connection =>
      SQL(
        """
         select * from user where 
         email = {email} and password = {password}
        """
      ).on(
        "email" -> email,
        "password" -> password
      ).as(simple.singleOpt)
    }
  }
   
  /**
   * Create a User.
   */
  def create(user: User): User = {
    db.withConnection { implicit connection =>
      SQL(
        """
          insert into user values (
            {email}, {name}, {password}
          )
        """
      ).on(
        "email" -> user.email,
        "name" -> user.name,
        "password" -> user.password
      ).executeUpdate()
      
      user
    }
  }
  
}
