import kotlinx.android.synthetic.main.saved_article_items.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS

fun currentDate(): Date {
    val calendar = Calendar.getInstance()
    return calendar.time
}

fun getTimeAgo(date: Date): String {
    var time = date.time
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = currentDate().time
    if (time > now || time <= 0) {
        return "in the future"
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> "moments ago"
        diff < 2 * MINUTE_MILLIS -> "a minute ago"
        diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
        diff < 2 * HOUR_MILLIS -> "an hour ago"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
        diff < 48 * HOUR_MILLIS -> "yesterday"
        else -> "${diff / DAY_MILLIS} days ago"
    }
}

fun main() {

//    val c=Calendar.getInstance()
//    val date =c.time
//    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss a", Locale.getDefault())
//    val strDate: String = dateFormat.format(date)
//
//
//    try {
//        val dateFormat1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss a", Locale.getDefault())
//        val pasTime = dateFormat1.parse(strDate)
//        val agoTime = getTimeAgo(pasTime!!)
//        print(agoTime)
//    }catch (e:Exception){
//
//    }
//    val formatted =  "2020-07-14T20:40:23Z"
//    try {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'K:mm:ss a", Locale.getDefault())
//        val pasTime = dateFormat.parse(formatted)
//        val agoTime = getTimeAgo(pasTime!!)
//       println(agoTime)
//    }catch (e:Exception){
//
//    }

//    val date = Calendar.getInstance().timeInMillis
    // val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
//    val strDate = "2020-07-15 07:54:02 PM"
//    println(strDate)
//    try {
//        val dateFormat1 = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
//        val pasTime = dateFormat1.parse(strDate)
//        val agoTime = getTimeAgo(pasTime!!)
//
//        if(agoTime == "7 minutes ago"){
//            println("time expired")
//        }
//        else{
//            println(agoTime)
//        }
//
//    }catch (e:Exception){
//
//    }
    val w = 15
    println(w / 5)
}