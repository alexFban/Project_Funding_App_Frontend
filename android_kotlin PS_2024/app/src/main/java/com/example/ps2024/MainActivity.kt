package com.example.ps2024

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.beust.klaxon.Klaxon
import com.example.ps2024.model.Weather
import com.example.ps2024.service.WebService.instance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


/**
 * Main activity launched by default when the app is open
 * This declaration is done within Manifest.xml
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var loadingBar: View? = null
    private var isCreator: Boolean = false
    private var userId: Long = -1
    private lateinit var fab: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var loginLayout: View
    private lateinit var tLayout: View
    private lateinit var pLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.visibility = View.GONE

        setSupportActionBar(toolbar)
        //weatherValueTextView = findViewById(R.id.weatherValue)
        loadingBar = findViewById(R.id.progressBar)
        loadingBar!!.visibility = View.GONE
        fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.visibility = View.GONE

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val loginButton: Button = findViewById(R.id.login_button)
        val cancelButton: Button = findViewById(R.id.cancel_button)
        val registerPageButton: Button = findViewById(R.id.goto_register_button)
        val backButton: Button = findViewById(R.id.back_button)
        val registerButton: Button = findViewById(R.id.register_button)
        val createButton: Button = findViewById(R.id.create_button)
        val projectCancelButton: Button = findViewById(R.id.project_cancel)

        val usernameET: EditText = findViewById(R.id.login_user)
        val passwordET: EditText = findViewById(R.id.login_pass)

        loginLayout = findViewById<View>(R.id.loginLayout)
        val registerLayout = findViewById<View>(R.id.registerLayout)
        registerLayout.visibility = View.GONE
        val createProjectLayout = findViewById<View>(R.id.createProjectLayout)
        createProjectLayout.visibility = View.GONE
        tLayout = findViewById(R.id.tableLayout)
        tLayout.visibility = View.GONE
        pLayout = findViewById(R.id.projectLayout)
        pLayout.visibility = View.GONE

        loginButton.setOnClickListener{
            GlobalScope.launch {
                if (Looper.myLooper() == null) {
                    Looper.prepare()
                }

                runOnUiThread{loadingBar!!.visibility = View.VISIBLE}
                if(verifyLogin(usernameET.text.toString(), passwordET.text.toString())) {
                    Toast.makeText(applicationContext,
                            "Logged-in successfully",Toast.LENGTH_SHORT).show();

                    runOnUiThread(){
                        toolbar.visibility = View.VISIBLE
                        if (isCreator) {
                            fab.visibility = View.VISIBLE
                        }
                        loginLayout.visibility = View.GONE
                    }
                }else{
                    Toast.makeText(applicationContext,
                            "Wrong Credentials",Toast.LENGTH_SHORT).show();
                }
                runOnUiThread{loadingBar!!.visibility = View.GONE}
            }
            }

        cancelButton.setOnClickListener{
                finish();
            }

        registerPageButton.setOnClickListener{
            runOnUiThread{
                loginLayout.visibility = View.GONE
                registerLayout.visibility = View.VISIBLE
            }
        }

        backButton.setOnClickListener{
            runOnUiThread{
                loginLayout.visibility = View.VISIBLE
                registerLayout.visibility = View.GONE
            }
        }

        registerButton.setOnClickListener{
            GlobalScope.launch {
                runOnUiThread{
                    loadingBar!!.visibility = View.VISIBLE
                }
                val message = register()

                if (Looper.myLooper() == null) {
                    Looper.prepare()
                }
                Toast.makeText(applicationContext,
                        message,Toast.LENGTH_SHORT).show();


                runOnUiThread(){
                    loadingBar!!.visibility = View.GONE
                    if(message == "Account created") {
                        registerLayout.visibility = View.GONE
                        loginLayout.visibility = View.VISIBLE
                    }
                }
            }
        }

        fab.setOnClickListener {
            runOnUiThread{
                tLayout.visibility = View.GONE
                pLayout.visibility = View.GONE
                createProjectLayout.visibility = View.VISIBLE
            }
        }

        createButton.setOnClickListener{
            GlobalScope.launch {
                runOnUiThread{
                    loadingBar!!.visibility = View.VISIBLE
                }

                val message = addProject()

                if (Looper.myLooper() == null) {
                    Looper.prepare()
                }
                Toast.makeText(applicationContext,
                        message,Toast.LENGTH_SHORT).show();

                runOnUiThread{
                    loadingBar!!.visibility = View.GONE
                    if (message == "Project created") {
                        createProjectLayout.visibility = View.GONE
                    }
                }
            }
        }

        projectCancelButton.setOnClickListener{
            runOnUiThread{
                createProjectLayout.visibility = View.GONE
            }
        }

        makeTheCall()
    }

    private fun makeTheCall() {
        val callback = object : Callback<Weather?> {
            override fun onResponse(call: Call<Weather?>, response: Response<Weather?>) {
                val temp = response.body()!!.main!!.temp!! - 273.15
                //weatherValueTextView!!.text = df2.format(temp) + "°C"
            }

            override fun onFailure(call: Call<Weather?>, t: Throwable) {
                Log.e("WeatherError", "error on Weather call: $t")
            }

        }
        instance?.weather?.enqueue(callback)
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    enum class Entity {User, Project}

    private fun addPledge(projectId: Long) : String{
        val url = "http://10.0.2.2:8080/api/user_projects"

        val client = OkHttpClient()

        val json = """
            {
                "user":{
                    "id":"$userId"
                },
                "project":{
                    "id":"$projectId"
                }
            }
        """.trimIndent()
        val formBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json
        )
        val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            return "Pledge error"
        }

        client.dispatcher.executorService.shutdown()
        return "Project pledged successfully"
    }

    private fun addProject() : String{
        val url = "http://10.0.2.2:8080/api/projects"

        val client = OkHttpClient()

        val name = (findViewById<EditText>(R.id.create_name)).text.toString()
        val fundingGoal = (findViewById<EditText>(R.id.create_goal)).text.toString()
        val description = (findViewById<EditText>(R.id.create_description)).text.toString()

        if(name.isEmpty() || fundingGoal.isEmpty() || description.isEmpty()) {
            return "Empty field"
        }

        val json = """
            {
            "name":"$name",
            "money_raised":0,
            "funding_goal":"$fundingGoal",
            "description":"$description"
            }
        """.trimIndent()
        val formBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json
        )
        val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            return "Project creation error"
        }

        client.dispatcher.executorService.shutdown()
        return "Project created"
    }

    private fun register() : String{
        val url = "http://10.0.2.2:8080/api/users"

        val client = OkHttpClient()

        val username = (findViewById<EditText>(R.id.reg_username)).text.toString()
        val password = (findViewById<EditText>(R.id.reg_password)).text.toString()
        val rePassword = (findViewById<EditText>(R.id.reg_repassword)).text.toString()
        val fName = (findViewById<EditText>(R.id.reg_fname)).text.toString()
        val lName = (findViewById<EditText>(R.id.reg_lname)).text.toString()
        val email = (findViewById<EditText>(R.id.reg_email)).text.toString()
        val type = if ((findViewById<CheckBox>(R.id.checkbox_creator)).isChecked) "creator" else "sponsor"

        if (password != rePassword) {
            return "Passwords don't match"
        }

        if(username.isEmpty() || password.isEmpty() || rePassword.isEmpty() ||
                fName.isEmpty() || lName.isEmpty() || email.isEmpty()) {
            return "Empty field"
        }

        val json = """
            {
            "username":"$username",
            "password":"$password",
            "firstName":"$fName",
            "lastName":"$lName",
            "email":"$email",
            "phone_number":-1,
            "type":"$type"
            }
        """.trimIndent()
        val formBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json
        )
        val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            return "Registration error"
        }

        client.dispatcher.executorService.shutdown()
        return "Account created"
    }

    private fun verifyLogin(username: String, password: String) : Boolean {
        val url = "http://10.0.2.2:8080/api/users/data/$username"

        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            return false
        }
        val json = response.body!!.string()
        val user = Klaxon().parse(json) as User?
        client.dispatcher.executorService.shutdown()

        if (user == null) {
            return false
        }

        isCreator = (user.type == "creator")
        userId = user.id
        return user.password == password
    }

    private fun getUsers(json: String) : List<User>? {return Klaxon().parseArray<User>(json)}
    private fun getProjects(json: String) : List<Project>? {return Klaxon().parseArray<Project>(json)}

    private fun getData(type : Entity): List<Any> {
        val url: String = when(type) {
            Entity.User -> {
                "http://10.0.2.2:8080/api/users"
            }
            Entity.Project -> {
                "http://10.0.2.2:8080/api/projects"
            }
        }

        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()

        Log.i("Thread", Thread.currentThread().toString())

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            System.err.println("Response not successful")
        }
        val json = response.body!!.string()

        val myData : ArrayList<Any> = when(type) {
            Entity.User -> {
                ArrayList(getUsers(json) as List<User>)
            }
            Entity.Project -> {
                ArrayList(getProjects(json) as List<Project>)
            }
        }
        Log.i("DataIn", myData.toString())
        Log.i("Thread", Thread.currentThread().toString())

        // Shutdown the executor as soon as the request is handled
        client.dispatcher.executorService.shutdown()

        return myData
    }

    private fun seeProject(project: Project) {
        val projectTitle = findViewById<TextView>(R.id.projectTitle)
        projectTitle!!.text = project.name

        val progress: ProgressBar = findViewById(R.id.Prog)
        val totalGoal = if (project.funding_goal <= 0) 1 else project.funding_goal
        val value = (project.money_raised * 100) / totalGoal
        progress.progress = value

        val projectDescription = findViewById<TextView>(R.id.projectDescription)
        projectDescription!!.text = project.description

        val pledgeButton: Button = findViewById(R.id.pledge_button)
        if (isCreator) {
            pledgeButton.visibility = View.GONE
        }
        else {
            pledgeButton.setOnClickListener {
                GlobalScope.launch {
                    runOnUiThread{
                        loadingBar!!.visibility = View.VISIBLE
                    }
                    val message = addPledge(project.id)

                    if (Looper.myLooper() == null) {
                        Looper.prepare()
                    }
                    Toast.makeText(applicationContext,
                            message,Toast.LENGTH_SHORT).show()

                    runOnUiThread{
                        loadingBar!!.visibility = View.GONE
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val instance = this
        val id = item.itemId

        tLayout.visibility = View.GONE////////////////////////////////////////IMPORTANT////////////////////////////////////////////

        pLayout.visibility = View.GONE

        val stk = findViewById<View>(R.id.tableMain) as TableLayout
        stk.removeAllViews()
        when (id) {
            R.id.nav_projects -> {
                GlobalScope.launch {
                    runOnUiThread {
                        tLayout.visibility = View.VISIBLE
                        loadingBar!!.visibility = View.VISIBLE
                    }
                    val data = getData(Entity.Project) as List<Project>
                    //Log.i("DataOut", data)
                    runOnUiThread {
                        loadingBar!!.visibility = View.GONE
                        val tableRow = TableRow(instance)

                        val tHeaders = listOf("No.", "Name", "")

                        //Table Headers
                        for(header in tHeaders) {
                            val textView = TextView(instance)
                            textView.text = header
                            textView.setTextColor(Color.BLACK)
                            textView.textSize = 20F
                            textView.setPadding(30, 5, 30, 5)
                            tableRow.addView(textView)
                        }
                        //End of table headers

                        //Add to table layout
                        stk.addView(tableRow)

                        for(i in data.indices) {
                            val nTableRow = TableRow(instance)
                            nTableRow.setPadding(0, 0, 0, 20)

                            val nTextView = TextView(instance)
                            val indexHeader = " " + (i + 1)
                            nTextView.text = indexHeader
                            nTextView.setTextColor(Color.BLACK)
                            nTextView.gravity = Gravity.CENTER
                            nTableRow.addView(nTextView)

                            val nTextView2 = TextView(instance)
                            nTextView2.text = data[i].name
                            nTextView2.setTextColor(Color.BLACK)
                            nTextView2.gravity = Gravity.CENTER
                            nTableRow.addView(nTextView2)

                            val btn = Button(instance)
                            val gradientDrawable = GradientDrawable()
                            gradientDrawable.cornerRadius = 50F
                            gradientDrawable.setColor(Color.LTGRAY)
                            btn.background = gradientDrawable

                            val buttonText = "View"
                            btn.text = buttonText
                            btn.setOnClickListener {
                                tLayout.visibility = View.GONE
                                pLayout.visibility = View.VISIBLE

                                seeProject(data[i])
                            }

                            nTableRow.addView(btn)
                            stk.addView(nTableRow)
                        }
                    }
                }
            }
            R.id.log_out -> {
                runOnUiThread(){
                    toolbar.visibility = View.GONE
                    fab.visibility = View.GONE
                    loginLayout.visibility = View.VISIBLE
                }
            }
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        private val df2 = DecimalFormat("#.##")
    }
}

data class User(
        val id: Long,
        val username: String,
        val password: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val phone_number: Int,
        val type: String,
)

data class Project(
        val id: Long,
        val name: String,
        val money_raised: Int,
        val funding_goal: Int,
        val description: String,
)