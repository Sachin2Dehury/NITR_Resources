package github.sachin2dehury.nitrresources.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import github.sachin2dehury.nitrresources.R
import github.sachin2dehury.nitrresources.fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.system.exitProcess

object Core {
    lateinit var fragmentManager: FragmentManager

    var streamYr = 0

    private var branch = "Trash"
    private var year = "Trash"
    var stream = "Trash"

    val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference

    private val trash = mutableMapOf<String, DocDetails>()
    private val notes = mutableMapOf<String, DocDetails>()
    private val assignment = mutableMapOf<String, DocDetails>()
    private val slides = mutableMapOf<String, DocDetails>()
    private val lab = mutableMapOf<String, DocDetails>()

    fun changeFragment(fragment: Fragment) {
        clearList()
        fragmentManager.beginTransaction().apply {
            replace(R.id.navFragment, fragment)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(fragment.javaClass.simpleName)
            commit()
        }
    }

    private fun clearList() {
        notes.clear()
        assignment.clear()
        slides.clear()
        lab.clear()
    }

    fun listSelector(item: Int): List<String> {
        return when (item) {
            STREAM_LIST -> streams
            YEAR_LIST -> years
            B_ARCH_LIST -> bArch
            B_TECH_LIST -> bTech
            M_TECH_LIST -> mTech
            MSC_LIST -> msc
            INT_MSC_LIST -> intMsc
            else -> noList
        }
    }

    fun listPredictor(item: Int, position: Int): Int {
        return when (item) {
            STREAM_LIST -> B_ARCH_LIST + position
            in B_ARCH_LIST..M_TECH_LIST -> YEAR_LIST
            else -> 0
        }
    }

    fun dataSetter(item: Int, position: Int) {
        when (item) {
            STREAM_LIST -> {
                streamYr = streamYears[position]
                stream = streams[position]
            }
            YEAR_LIST -> {
                if (position == 0) {
                    when (stream) {
                        streams[1] -> branch = "All"
                        streams[2] -> branch = "All"
                    }
                }
                year = years[position]
            }
            B_ARCH_LIST -> branch = bArch[position]
            B_TECH_LIST -> branch = bTech[position]
            M_TECH_LIST -> branch = mTech[position]
            MSC_LIST -> branch = msc[position]
            INT_MSC_LIST -> branch = intMsc[position]
        }
    }

    fun openLink(link: String, context: Context) {
        val url = Uri.parse(link)!!
        val intent = Intent(Intent.ACTION_VIEW, url)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun getMenuIcon(menu: PopupMenu) {
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(menu)!!
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Log.w("Catch", e.toString())
        }
    }

    fun pageSelector(position: Int): MutableMap<String, DocDetails> {
        return when (NOTES_LIST + position) {
            NOTES_LIST -> notes
            ASSIGNMENT_LIST -> assignment
            SLIDES_LIST -> slides
            LAB_LIST -> lab
            else -> trash
        }
    }

//    fun changeActivity(
//        context: Context,
//        login: Boolean = false,
//        file: String = "",
//        rename: Boolean = false,
//        pageIndex: Int = 0
//    ) {
//        val intent = Intent(context, NavActivity::class.java).apply {
//            putExtra("Login", login)
//            putExtra("File", file)
//            putExtra("Rename", rename)
//            putExtra("PageIndex", pageIndex)
//        }
//        context.startActivity(intent)
//    }

    fun navDrawerMenu(item: MenuItem, context: Context) {
        when (item.itemId) {
            R.id.home -> changeFragment(ListFragment(STREAM_LIST))
            R.id.book -> openLink(BOOK_LINK, context)
            R.id.nitris -> openLink(QUESTION_LINK, context)
            R.id.mail -> openLink(MAIL_LINK, context)
            R.id.news -> openLink(TELEGRAM_NEWS_LINK, context)
        }
    }

    private fun signOut() {
        when (firebaseAuth.currentUser) {
            null -> changeFragment(LoginFragment())
            else -> firebaseAuth.signOut()
        }
    }

    fun optionMenu(item: MenuItem) {
        when (item.itemId) {
            R.id.settings -> changeFragment(SettingsFragment())
            R.id.myDocs -> changeFragment(LoginFragment())
            R.id.user -> signOut()
            R.id.about -> changeFragment(AboutFragment())
            R.id.exit -> exitProcess(0)
        }
    }

    fun popUpMenu(
        item: MenuItem,
        context: Context,
        current: String,
        index: Int,
    ) {
        when (item.itemId) {
            R.id.rename -> changeFragment(RenameFragment(current, true, index))
            R.id.delete -> deleteDoc(current, index)
            R.id.download -> deleteDoc(current, index)
            R.id.share -> shareDoc(context, current, index)
        }
    }

    private fun shareDoc(context: Context, current: String, item: Int) {
        val list = pageSelector(item)
        val intent = Intent(Intent.ACTION_SEND)
        intent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check Out This : ${list[current]!!.name}")
            putExtra(Intent.EXTRA_TEXT, list[current]!!.url)
        }
        context.startActivity(Intent.createChooser(intent, "Share link!"))
    }

    fun getList(item: Int) = CoroutineScope(Dispatchers.IO).launch {
        val list = pageSelector(item)
        val path = "$college/$stream/$year/$branch/${pages[item - NOTES_LIST]}"
        val documents = firebaseFireStore.collection(path).get().await()!!.documents
        for (document in documents) {
            val doc = document.toObject(DocDetails::class.java)!!
            list[document.id] = doc
        }
    }

    fun uploadDoc(file: Uri, doc: DocDetails, item: Int) =
        CoroutineScope(Dispatchers.IO).launch {
            val path = "$college/$stream/$year/$branch/${pages[item]}"
            val list = pageSelector(item)
            val docId = firebaseFireStore.collection(path).add(doc).await()!!.id
            val storeReference = firebaseStorage.child("$path/$docId.pdf")
            val docRef = firebaseFireStore.collection(path).document(docId)
            storeReference.putFile(file).await()

            doc.url = storeReference.downloadUrl.await().toString()
            doc.contributor = firebaseAuth.currentUser.toString()
            storeReference.metadata.await().apply {
                doc.size = sizeBytes.toDouble() / MB
                doc.time = updatedTimeMillis
            }
            firebaseFireStore.runBatch { batch ->
                batch.set(docRef, doc)
            }
            list[docId] = doc
        }

    fun updateDocList(item: Int) = CoroutineScope(Dispatchers.Main).launch {
        val list = pageSelector(item)
        val path = "$college/$stream/$year/$branch/${pages[item]}"
        firebaseFireStore.collection(path).addSnapshotListener { querySnapshot, _ ->
            for (change in querySnapshot!!.documentChanges) {
                val doc = (change.document.toObject(DocDetails::class.java))
                when (change.type) {
                    DocumentChange.Type.ADDED -> list[change.document.id] = doc
                    DocumentChange.Type.MODIFIED -> list[change.document.id] = doc
                    DocumentChange.Type.REMOVED -> list.remove(change.document.id)
                }

            }
        }
    }


    fun renameDoc(docId: String, doc: DocDetails, item: Int) =
        CoroutineScope(Dispatchers.IO).launch {
            val list = pageSelector(item)
            val path = "$college/$stream/$year/$branch/${pages[item]}"
            val docRef = firebaseFireStore.collection(path).document(docId)
            firebaseFireStore.runTransaction { batch ->
                batch.set(docRef, doc)
            }
            list[docId] = doc
        }

    private fun deleteDoc(docId: String, item: Int) =
        CoroutineScope(Dispatchers.IO).launch {
            val list = pageSelector(item)
            val path = "$college/$stream/$year/$branch/${pages[item]}"
            if (list[docId]!!.contributor == firebaseAuth.currentUser.toString()) {
                firebaseFireStore.collection(path).document(docId).delete().await()
                firebaseFireStore.collection("Trash").add(list[docId]!!)
                    .await().id
                list.remove(docId)
            }
        }

    @SuppressLint("CommitPrefEdits")
    fun saveAppData(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putString("Stream", stream)
            putString("Email", firebaseAuth.currentUser!!.email!!)
            apply()
        }
    }

    fun loadAppData(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            stream = getString("Stream", "Trash")!!
            streamYr = streams.indexOf(stream)
        }
    }
}