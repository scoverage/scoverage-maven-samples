import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

  "Application" should {

    "work from within a browser" in new WithBrowser {
        browser.goTo("http://localhost:" + port)

        browser.$("header a").first.getText must equalTo("Zentasks")
        browser.$("#email").text("guillaume@sample.com")
        browser.$("#password").text("secret111")
        browser.$("#loginbutton").click()
        browser.pageSource must contain("Invalid email or password")

        browser.$("#email").text("guillaume@sample.com")
        browser.$("#password").text("secret")
        browser.$("#loginbutton").click()
        browser.$("dl.error").size must equalTo(0)
        browser.pageSource must not contain("Sign in")
        browser.pageSource must contain("guillaume@sample.com")
        browser.pageSource must contain("Logout")

        val items = browser.$("li")
        items.size must equalTo(15)
        items.get(3).getText must contain("Website Delete")
    }

  }

}
