package com.aihuishou.business.ocr.entity

/**
 * @ClassName  RecognitionResultBean
 * @author liujc
 * @date 2019/11/22
 * @Description (这里用一句话描述这个类的作用)
 */
class RecognitionResultBean {

    var log_id: Long = 0
    var words_result_num: Int = 0
    var words_result: List<WordsResultBean>? = null

    override fun toString(): String {
        return "RecognitionResultBean{" +
                "log_id=" + log_id +
                ", words_result_num=" + words_result_num +
                ", words_result=" + words_result +
                '}'.toString()
    }

    class WordsResultBean {

        var words: String? = null

        override fun toString(): String {
            return "WordsResultBean{" +
                    "words='" + words + '\''.toString() +
                    '}'.toString()
        }
    }
}