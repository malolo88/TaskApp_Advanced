package jp.techacademy.arai.yukina.taskappadvanced

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class CatAdapter(context: Context): BaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    var catList = mutableListOf<Category>()

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return catList.size
    }

    override fun getItem(position: Int): Any {
        return catList[position]
    }

    override fun getItemId(position: Int): Long {
        return catList[position].categoryId.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: mLayoutInflater.inflate(android.R.layout.simple_spinner_item, null)

        //Spinnerでカテゴリーの名前を表示させる
        val textView1 = view.findViewById<TextView>(android.R.id.text1)
        textView1.text = catList[position].categoryName

        return view
    }


}