package jp.techacademy.arai.yukina.taskappadvanced

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import io.realm.RealmChangeListener
import io.realm.Sort
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.app.AlarmManager
import android.app.PendingIntent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner


const val EXTRA_TASK = "jp.techacademy.taro.kirameki.taskapp.TASK"

class MainActivity : AppCompatActivity() {
    private lateinit var mRealm: Realm
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView()
            reloadSpinnerView()
        }
    }

    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var mCatAdapter: CatAdapter
    private val mTask: Task? = null
    private val mCategory: Category? = null
    private  var mCategoryId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fab.setOnClickListener { view ->
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            startActivity(intent)
            reloadSpinnerView()
        }

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)

        // ListViewの設定
        mTaskAdapter = TaskAdapter(this@MainActivity)
        mCatAdapter = CatAdapter(this@MainActivity)

        // ListViewをタップしたときの処理
        listView1.setOnItemClickListener { parent, _, position, _ ->
            // 入力・編集する画面に遷移させる
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        // ListViewを長押ししたときの処理
        listView1.setOnItemLongClickListener { parent, _, position, _ ->
            // タスクを削除する
            val task = parent.adapter.getItem(position) as Task

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            builder.setPositiveButton("OK"){_, _ ->
                val results = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
                val resultPendingIntent = PendingIntent.getBroadcast(
                    this@MainActivity,
                    task.id,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(resultPendingIntent)

                reloadListView()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }

        reloadListView()

        if(mCategory?.categoryId == null){
            val category = Category()
            category.categoryName = "すべて"
            category.categoryId = 0
            mRealm.beginTransaction()
            mRealm.copyToRealmOrUpdate(category)
            mRealm.commitTransaction()
        }

        //カテゴリーの表示
        reloadSpinnerView()
        //カテゴリーが選択されたとき
        spinner_search.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //選択されたカテゴリーの情報を取得する
                mCategoryId = mCatAdapter.getItemId(position).toInt()

                val category = mTask?.category

                //Realmデータベースから、「カテゴリーの検索結果」を取得
                var searchResults = mRealm.where(Task::class.java).equalTo("category", mCategoryId).findAll()


                if(mCategoryId == 0){
                    reloadListView()
                } else {
                    //カテゴリーの検索結果を、TaskList としてセットする
                    mTaskAdapter.taskList = mRealm.copyFromRealm(searchResults)

                    //TaskのListView用のアダプタに渡す
                    listView1.adapter = mTaskAdapter

                    //表示を更新するために、アダプターにデータが変更されたことを知らせる
                    mTaskAdapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }

    }

    private fun reloadListView() {
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val taskRealmResults = mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)

        // 上記の結果を、TaskList としてセットする
        mTaskAdapter.taskList = mRealm.copyFromRealm(taskRealmResults)

        // TaskのListView用のアダプタに渡す
        listView1.adapter = mTaskAdapter

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }

    private fun reloadSpinnerView() {
        val spinner: Spinner = findViewById(R.id.spinner_search)

        //Realmインスタンスを取得
        mRealm = Realm.getDefaultInstance()

        //カテゴリーの情報をIDの降順で取得する
        val catRealmResults = mRealm.where(Category::class.java).findAll().sort("categoryId", Sort.ASCENDING)

        // 上記の結果を、CatList としてセットする
        mCatAdapter.catList = mRealm.copyFromRealm(catRealmResults)

        //spinner用のアダプタに渡す
        spinner.adapter = mCatAdapter

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mCatAdapter.notifyDataSetChanged()

    }


}

