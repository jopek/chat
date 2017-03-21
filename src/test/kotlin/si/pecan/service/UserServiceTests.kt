package si.pecan.service

import com.winterbe.expekt.should
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import si.pecan.DuplicateUserException
import si.pecan.Stubs.Companion.VALID_USER
import si.pecan.model.User
import si.pecan.services.UserService
import javax.transaction.Transactional

/**
 * Created by matjaz on 3/21/17.
 */

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
class UserServiceTests {

    @Autowired
    lateinit var userService: UserService

    @Test
    fun testUserCreation() {
        val user:User = userService.getOrCreate("newUser")
        user.id.should.not.be.`null`
    }

    fun testUserUniqueness() {
        val first = userService.getOrCreate("newUser")
        val second = userService.getOrCreate("newUser")
        first.id.should.equal(second.id)
    }

    
}