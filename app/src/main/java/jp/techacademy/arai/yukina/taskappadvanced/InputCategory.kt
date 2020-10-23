package jp.techacademy.arai.yukina.taskappadvanced

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import io.realm.Realm
import kotlinx.android.synthetic.main.category_input.*


class InputCategory : AppCompatActivity() {

    private var mCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_category)

        // ActionBarを設定する
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        add_button.setOnClickListener(){
            addCategory()
            finish()
        }

    }

    private fun addCategory(){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        if (mCategory == null) {
            //カテゴリーの新規作成
            mCategory = Category()

            val categoryRealmResults = realm.where(Category::class.java).findAll()

            val identifier: Int =
                if (categoryRealmResults.max("categoryId") != null) {
                    categoryRealmResults.max("categoryId")!!.toInt() + 1
                } else {
                    1 //0は「すべて」を表示するために使用しているため
                }
            mCategory!!.categoryId = identifier
        }

        val category = category_editText.text.toString()

        mCategory!!.categoryName = category

        realm.copyToRealmOrUpdate(mCategory!!)
        realm.commitTransaction()

        realm.close()

    }
}
