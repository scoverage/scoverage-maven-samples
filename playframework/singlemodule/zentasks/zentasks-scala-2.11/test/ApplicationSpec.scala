import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {
  
  "Application" should {
    
    "go to login page without credentials" in new WithApplication(_.configure(inMemoryDatabase())) {
      val result  = route(app, FakeRequest( GET, "/")).get
      status(result) must equalTo(303)
    }

    "list the secured product page with credentials" in new WithApplication(_.configure(inMemoryDatabase())) {
      val result  = route(app, FakeRequest( GET, "/").withSession("email"->"guillaume@sample.com")).get
      status(result) must equalTo(200)
    }
    
  }
}
