package jp.techacademy.arai.yukina.taskappadvanced

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_input_category.*
import io.realm.Realm
import android.widget.Toast
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.category_input.*


class InputCategory : AppCompatActivity() {

    private var mCat: Category? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_category)

        add_button.setOnClickListener(){
            addCategory()
            Toast.makeText(applicationContext, "登録しました", Toast.LENGTH_LONG).show()

            finish()
        }


    }


    private fun addCategory(){
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        if (mCat == null) {
            //カテゴリーの新規作成
            mCat = Category()

            val categoryRealmResults = realm.where(Category::class.java).findAll()

            val identifier: Int =
                if (categoryRealmResults.max("categoryId") != null) {
                    categoryRealmResults.max("categoryId")!!.toInt() + 1
                } else {
                    0
                }
            mCat!!.categoryId = identifier
        }

        val category = category_editText.text.toString()

        mCat!!.categoryName = category

        realm.copyToRealmOrUpdate(mCat!!)
        realm.commitTransaction()

        realm.close()

    }
}
