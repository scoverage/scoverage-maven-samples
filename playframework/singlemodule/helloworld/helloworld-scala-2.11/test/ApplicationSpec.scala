import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(app, FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render an empty form on index" in new WithApplication {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
    }

    "send BadRequest on form error" in new WithApplication {
      val home = route(app, FakeRequest(GET, "/hello?name=Bob&repeat=xx")).get

      status(home) must equalTo(BAD_REQUEST)
      contentType(home) must beSome.which(_ == "text/html")
    }

    "say hello" in new WithApplication {
      val home = route(app, FakeRequest(GET, "/hello?name=Bob&repeat=10")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
    }

  } 

}
