package jp.techacademy.arai.yukina.taskappadvanced

import java.io.Serializable
import java.util.Date
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Category: RealmObject(), Serializable  {
    var categoryName:String = "" //カテゴリー

    // id をプライマリーキーとして設定
    @PrimaryKey
    var categoryId: Int = 0
}