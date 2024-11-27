package com.coding.aplikasistoryapp.view.login

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coding.aplikasistoryapp.R
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginSuccessAndLogout() {

        onView(withId(R.id.emailEditText))
            .perform(typeText("cobabudi@email.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("12345678"), closeSoftKeyboard())

        onView(withId(R.id.loginButton))
            .perform(click())

        onView(isRoot()).perform(waitFor(11000))

        onView(withContentDescription("More options"))
            .perform(click())

        onView(withText("Log Out"))
            .perform(click())

    }

    @Test
    fun testLoginFailed() {

        onView(withId(R.id.emailEditText))
            .perform(typeText("wronguser@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("wrongpassword"), closeSoftKeyboard())

        onView(withId(R.id.loginButton))
            .perform(click())

        onView(withId(R.id.login_activity))
            .check(matches(isDisplayed()))
    }

    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "$delay milliseconds."
            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}