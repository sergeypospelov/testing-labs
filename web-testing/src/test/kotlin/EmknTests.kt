import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

class EmknTests {
    lateinit var driver: WebDriver

    @BeforeEach
    fun initDriver() {
        driver = chrome()
    }

    @AfterEach
    fun closeDriver() {
        driver.quit()
    }

    @Test
    fun testAssignmentsWithoutLogin() {
        driver.get("https://emkn.ru/learning/assignments/")
        assertEquals("https://emkn.ru/login/?next=/learning/assignments/", driver.currentUrl)
    }


    @Test
    fun testCorrectLogin() {
        EmknUtils.login(driver)

        assertEquals("МКН СПбГУ", driver.title)
    }

    @Test
    fun testIncorrectLogin() {
        EmknUtils.login(driver, passwordData = "qwerty")

        val errorSpan = driver
            .findElement(By.id("sign-in"))
            .findElement(By.className("error-message"))

        assertTrue(errorSpan.isEnabled)
    }

    @Test
    fun testTaskView() {
        EmknUtils.login(driver)

        val link = driver.findElement(By.linkText("Соревнование по классификации изображений"))
        link.click()

        assertTrue("Соревнование по классификации изображений" in driver.title)
    }

    @Test
    fun testTeacherViewFromTaskView() {
        EmknUtils.login(driver)

        val link = driver.findElement(By.linkText("Соревнование по классификации изображений"))
        link.click()

        val teacherLink = driver.findElement(By.linkText("Александр Юрьевич Авдюшенко"))
        teacherLink.click()

        val header = driver.findElement(By.className("content-title"))


        assertEquals("Александр Юрьевич Авдюшенко", header.text)
    }

    @Test
    fun testCourseView() {
        EmknUtils.login(driver)

        val coursesLink = driver.findElement(By.partialLinkText("Мои курсы"))
        coursesLink.click()

        val courseLink = driver.findElement(By.linkText("Компьютерные сети"))
        courseLink.click()

        assertTrue("Компьютерные сети" in driver.title)

    }
}

fun chrome(): WebDriver {
    WebDriverManager.chromedriver().setup()
    return ChromeDriver()
}

private object EmknUtils {
    private val correctUsername = System.getenv("USER")
    private val correctPassword = System.getenv("PASSWD")

    fun login(
        driver: WebDriver,
        usernameData: String = correctUsername,
        passwordData: String = correctPassword
    ) {
        driver.get("https://emkn.ru/")

        val form = driver.findElement(By.id("sign-in"))
        val username = form.findElement(By.id("id_username"))
        val password = form.findElement(By.id("id_password"))
        val submit = form.findElement(By.cssSelector("input[type='submit']"))

        username.sendKeys(usernameData)
        password.sendKeys(passwordData)
        submit.click()
    }
}