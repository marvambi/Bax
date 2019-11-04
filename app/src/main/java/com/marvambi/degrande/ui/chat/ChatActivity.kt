package com.marvambi.degrande.ui.chat

import android.app.AlertDialog
import android.content.*
import com.google.gson.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.bumptech.glide.Glide
import com.flutterwave.raveandroid.RavePayManager
import com.marvambi.degrande.common.BaseActivity
import com.marvambi.degrande.common.option.centerCropOptions
import com.marvambi.degrande.datas.Author
import com.marvambi.degrande.datas.Message
import com.marvambi.degrande.extensions.getAndroidId
import com.marvambi.degrande.extensions.showToast
import com.stfalcon.chatkit.commons.ImageLoader
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_chat.*
import com.marvambi.degrande.datas.Messages
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveConstants
import com.marvambi.degrande.R
import com.marvambi.degrande.db.BWOpenDBHelper
import com.marvambi.degrande.db.HistoryMessage
import com.marvambi.degrande.db.MessageModel
import com.marvambi.degrande.ui.main.MessageHistoryAdapter
import kotlinx.android.synthetic.main.guest_info_layout.view.*
import org.joda.time.DateTime
import org.json.JSONArray
import java.util.*


class ChatActivity : BaseActivity(), ChatMvpView {

    override fun update(message: Message) {
        //Add Something to implement this
    }

    private lateinit var presenter: ChatPresenter<ChatMvpView>
    private lateinit var messageAdapter: MessageHistoryAdapter<Messages>

    private val userId by lazy { getAndroidId()}


    lateinit var realm: Realm
    var messageChanel: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Realm.init(this)


        realm = Realm.getDefaultInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("deviceinfo", Context.MODE_PRIVATE)

        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

        editor.putString("identity",getAndroidId())
        editor.apply()
        editor.commit()

        initTitle()

        Toast.makeText(this, "Getting $title ...", Toast.LENGTH_LONG).show()

        initViewAdapter()

        // get reference to service button
        val btnservice: Button = findViewById(com.marvambi.degrande.R.id.btn_service)
        // set on-click listener

        btnservice.setOnClickListener {

            //Check the department and provide service availability respective
            if (messageChanel !== null) {
                when(messageChanel) {
                    "Kitchen" -> { showToast("Kitchen Chosen") }
                    "Laundry" -> { showToast("Laundry Chosen")}
                    "Maintenance" -> { showToast("Maintenance Chosen")}
                    "Security" -> { showToast("Security Chosen")}
                }
            }
            // build Service Choise dialog
            val dialogBuilder = AlertDialog.Builder(this)
            // String array for alert dialog multi choice items
            val colorsArray = arrayOf("Black", "Orange", "Green", "Yellow", "White", "Purple")
            val kitchenServices = arrayOf("Barbacue", "Bread & Tea", "Green Tea", "Semo & White Soup", "Banana & Tiger nut Milk", "Blackcurrant Juice", "Fried Plantain and Beans", "Orange", "Fried Rice and White Chicken", "Mangoes", "Pineapple", "White Soup", "Pawpaw")
            val laundryServices = arrayOf("Shoe Shining", "Light Laundry", "Clothes Cleaning", "Pillow Replacement", "Bedsheets laying", "Mat Canvassing", "Bathroom Washing clothes", "Bathing soap", "Shampoo", "Light Clothe Pressing", "Suit Cleaning", "Belt Repair", "Pampas washing")
            // Boolean array for initial selected items
            val checkedColorsArray = booleanArrayOf(true,
                    false, // Green
                    true, // Yellow checked
                    false, // White
                    false, //Purple
                    true,
                    true, //Purple
                    true,
                    false, //Purple
                    true,
                    true,
                    true, //Purple
                    true
            )
            // Convert the color array to list
            val colorsList = Arrays.asList(*kitchenServices)
            //setTitle
            dialogBuilder.setTitle("Select The $messageChanel Service(s) item(s) you want")

            //set multichoice
            dialogBuilder.setMultiChoiceItems(kitchenServices, checkedColorsArray) {
                dialogBuilder, which, isChecked ->
                // Update the current focused item's checked status
                checkedColorsArray[which] = isChecked
                // Get the current focused item
                val currentItem = colorsList[which]
                // Notify the current action
                Toast.makeText(applicationContext, currentItem + " " + isChecked, Toast.LENGTH_SHORT).show()
            }
            // Set the positive/yes button click listener
            dialogBuilder.setPositiveButton("OK") {
                dialogBuilder, which ->
                // Do something when click positive button
                //mSlctdTxtTv.text = "Your preferred colors..... \n"
                //Toast.makeText(applicationContext, "You clicked OK", Toast.LENGTH_LONG).show()

                var servicesSelected: String = "\n"
                var shouldShowPaymentDialog: Int = 0
                for (i in checkedColorsArray.indices) {
                    val checked = checkedColorsArray[i]
                    if (checked) {
                        servicesSelected +=  colorsList[i] + "\n"
                        shouldShowPaymentDialog += 1
                        //mSlctdTxtTv.text = mSlctdTxtTv.text.toString() + colorsList[i] + "\n"
                    }
                }

                when(shouldShowPaymentDialog) {
                    0 -> {
                        Toast.makeText(applicationContext, "You Selected nothing!", Toast.LENGTH_LONG).show() }
                    in 1..colorsList.size -> {
                        Toast.makeText(applicationContext, "You Selected: " + shouldShowPaymentDialog + " Item(s)", Toast.LENGTH_LONG).show()
                        showGuestInfoDialog() }
                }
            }
            // Set the neutral/cancel button click listener
            dialogBuilder.setNeutralButton("Cancel") {
                dialogBuilder, which ->
                // Do something when click the neutral button
                Toast.makeText(applicationContext, "You chose to do nothing", Toast.LENGTH_LONG).show()
            }
            val dialog = dialogBuilder.create()
            // Display the alert dialog on interface
            dialog.show()
        }

        bindEvent()

        connect()

    }

    private fun showGuestInfoDialog() {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.guest_info_layout, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Profile Information")
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialogPayBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout
            val name = mDialogView.dialogNameEt.text.toString()
            val email = mDialogView.dialogEmailEt.text.toString()
            val phone = mDialogView.dialogPhoneEt.text.toString()

            val amount: Double = 0.0
            //Start the payment checkout here
            makePayment(name, email, phone, amount)
            //mainInfoTv.setText("Name:"+ name +"\nEmail: "+ email +"\nPassword: "+ password)
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }


    private fun initTitle() {
        title = intent.extras.getString("title")
        messageChanel = title?.toString()
    }


    private fun initViewAdapter() {
        messageAdapter = MessageHistoryAdapter(
                userId,
                getImageLoader()
        )
        messageAdapter.registerViewClickListener(R.id.messageUserAvatar, this)

        rvMessage.setAdapter(messageAdapter)
    }

    private fun bindEvent() {
        inputMessage.setInputListener(this)
    }


    private fun connect() {
       loading.visibility = View.VISIBLE
        inputMessage.visibility = View.GONE
        btn_service.visibility = View.GONE
        presenter.connect(Author(userId,userId), "$title/$userId")
    }

    override fun initPresenter() {
        presenter = ChatPresenter()
        presenter.attachView(this)
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
        realm.close()
    }

    override fun onSuccessConnect() {
        loading.visibility = View.GONE
        btn_service.visibility = View.VISIBLE
        inputMessage.visibility = View.VISIBLE
    }

    override fun onErrorConnect() {
        showToast("Error Connecting")
        finish()
    }

    override fun onSubmit(input: CharSequence?): Boolean {
        presenter.sendMessage(input)

        return true
    }

    override fun onReceiveMessage(message: Message) {
        addRecord(message)
        messageAdapter.addToStart(message, true)

    }

    override fun onMessageViewClick(view: View?, message: Message?) {}

    private fun getImageLoader() =
            ImageLoader { imageView ,url, view ->
                Glide.with(this)
                        .load(url)
                        .apply(centerCropOptions(R.drawable.ic_profile, R.drawable.ic_profile))
                        .into(imageView)
            }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo.isConnected
        } else false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {

            val message = data.getStringExtra("response")

            if (resultCode === RavePayActivity.RESULT_SUCCESS) {

                Toast.makeText(this, "SUCCESS $message", Toast.LENGTH_SHORT).show()

                //Handle Payment History Persistence here
                //Get handle on real instance to start recording transaction details
                realm.beginTransaction()

                realm.commitTransaction() //Commit the transaction details to real databases.
                //Start a realm transaction here with beginTransaction() method

                //End realm transaction with commitTransaction() method

            } else if (resultCode === RavePayActivity.RESULT_ERROR) {

                Toast.makeText(this, "ERROR $message", Toast.LENGTH_SHORT).show()

            } else if (resultCode === RavePayActivity.RESULT_CANCELLED) {

                Toast.makeText(this, "CANCELLED $message", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onPause() {
        Log.w("ChatActivity", "Pausing the Chat Activity")
        super.onPause()
    }

    fun addRecord(message: Message) {

        /**
         * Experiment
         */

        if (!isNetworkAvailable()) {
            Toast.makeText(applicationContext, "Oops... Please connect to a reliable network!", Toast.LENGTH_LONG).show()
        } else {

            val dbHandler = BWOpenDBHelper(this)
            val msgHistory = MessageModel(message.id, message.user, messageChanel!!, message.text, message.createdAt.time)
            //var result = dbHandler.insertMessage(msgHistory)
            val msgs = dbHandler.readMessage(messageChanel!!)
            when(dbHandler.insertMessage(msgHistory)) {
                true -> {
                    Log.w("COUNT", "There are ${msgs.count()} $messageChanel messages in the db")
                    Log.w("Added", msgHistory.text + " Added to database") }
                false -> {
                    Log.w("Error", "${msgHistory.text} could not be added to database")
                    Log.w("COUNT", "There are ${msgs.count()} $messageChanel messages in the db") }
            }

            /**
             * Remodel Message class from MessageModel to convert easily into MessageHolder's expectation
             */

            msgs.forEach {
                val msgHistory = Message(it.id, it.author, messageChanel!!, it.text, it.createAt)
                messageAdapter.deleteById(it.id)
                messageAdapter.addToStart(msgHistory, true)
            }
            messageAdapter.notifyDataSetChanged() //.addToEnd(msgs,true)
        }


    }

    private fun makePayment(name: String, email: String, phone: String, amount: Double) {
        RavePayManager(this)
                .setAmount(amount)
                .setEmail(email)
                .setCountry("NG")
                .setCurrency("NGN")
                .setfName("Customer").setlName(name)
                .setNarration("Payment for $messageChanel services by: $name -> $phone")
                .setPublicKey("FLWPUBK_TEST-1b7b1681fbe422cc6beebb9a356c6ac8-X")
                .setEncryptionKey("FLWSECK_TESTf15b39cc110b")
                .setTxRef(System.currentTimeMillis().toString() + "Ref")
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .onStagingEnv(true)
                .shouldDisplayFee(true)
                .showStagingLabel(true)
                .initialize()
    }


}
