package garcia.fernando.myfeelings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import garcia.fernando.myfeelings.utilities.CustomBarDrawable
import garcia.fernando.myfeelings.utilities.CustomCircleDrawable
import garcia.fernando.myfeelings.utilities.Emociones
import garcia.fernando.myfeelings.utilities.JSONFile
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var jsonFile : JSONFile? = null
    var veryHappy = 0.0f
    var happy = 0.0f
    var neutral = 0.0f
    var sad = 0.0f
    var verySad = 0.0f
    var data : Boolean = false
    var lista = ArrayList<Emociones>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jsonFile = JSONFile()
        fetchingData()

        if (!data){
            var emociones = ArrayList<Emociones>()
            val fondo = CustomCircleDrawable(this, emociones)
            this.findViewById<LinearLayout>(R.id.graph).background = fondo
            this.findViewById<View>(R.id.graphVeryHappy).background = CustomBarDrawable(this, Emociones("Muy feliz", 0.0f, R.color.mustard, veryHappy))
            this.findViewById<View>(R.id.graphHappy).background = CustomBarDrawable(this, Emociones("Feliz", 0.0f, R.color.orange, happy))
            this.findViewById<View>(R.id.graphNeutral).background = CustomBarDrawable(this, Emociones("Neutral", 0.0f, R.color.greenie, neutral))
            this.findViewById<View>(R.id.graphSad).background = CustomBarDrawable(this, Emociones("Triste", 0.0f, R.color.blue, sad))
            this.findViewById<View>(R.id.graphVerySad).background = CustomBarDrawable(this, Emociones("Muy triste", 0.0f, R.color.deepBlue, verySad))

        } else {
            actualizarGrafica()
            iconoMayoria()
        }

    }

    fun fetchingData(){
        try {
            var json : String = jsonFile?.getData(this) ?: ""
            if (json != ""){
                this.data = true
                var jsonArray : JSONArray = JSONArray(json)

                this.lista = parseJson(jsonArray)

                for (i in lista){
                    when(i.nombre){
                        "Muy feliz" -> veryHappy = i.total
                        "Feliz" -> happy = i.total
                        "Neutral" ->  neutral = i.total
                        "Triste" -> sad = i.total
                        "Muy triste" -> verySad = i.total
                    }
                }

            }else{
                this.data = false
            }
        } catch (e : JSONException) {
            e.printStackTrace()
        }
    }

    fun iconoMayoria(){
        if (happy > veryHappy && happy > neutral && happy > sad && happy > verySad) {
            this.findViewById<ImageView>(R.id.icon).setImageDrawable(resources.getDrawable(R.drawable.ic_happy))
        }
        if (veryHappy > happy && veryHappy > neutral && veryHappy > sad && veryHappy > verySad) {
            this.findViewById<ImageView>(R.id.icon).setImageDrawable(resources.getDrawable(R.drawable.ic_veryhappy))
        }
        if (neutral > happy && neutral > veryHappy && neutral > sad && neutral > verySad) {
            this.findViewById<ImageView>(R.id.icon).setImageDrawable(resources.getDrawable(R.drawable.ic_neutral))
        }
        if (sad > happy && sad > veryHappy && sad > neutral && sad > verySad) {
            this.findViewById<ImageView>(R.id.icon).setImageDrawable(resources.getDrawable(R.drawable.ic_sad))
        }
        if (verySad > happy && verySad > veryHappy && verySad > neutral && verySad > sad) {
            this.findViewById<ImageView>(R.id.icon).setImageDrawable(resources.getDrawable(R.drawable.ic_verysad))
        }
    }

    fun actualizarGrafica(){
        val total = veryHappy+happy+neutral+sad+verySad

        var pVH : Float = ((veryHappy * 100)/total).toFloat()
        var pH : Float = ((happy * 100)/total).toFloat()
        var pN : Float = ((neutral * 100)/total).toFloat()
        var pS : Float = ((sad * 100)/total).toFloat()
        var pVS : Float = ((verySad * 100)/total).toFloat()

        lista.clear()
        lista.add(Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        lista.add(Emociones("Feliz", pH, R.color.orange, happy))
        lista.add(Emociones("Neutral", pN, R.color.greenie, neutral))
        lista.add(Emociones("Triste", pS, R.color.blue, sad))
        lista.add(Emociones("Muy triste", pVS, R.color.deepBlue, verySad))

        val fondo = CustomCircleDrawable(this, lista)


        this.findViewById<View>(R.id.graphVeryHappy).background = CustomBarDrawable(this, Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        this.findViewById<View>(R.id.graphHappy).background = CustomBarDrawable(this, Emociones("Feliz", pH, R.color.orange, happy))
        this.findViewById<View>(R.id.graphNeutral).background = CustomBarDrawable(this, Emociones("Neutral", pN, R.color.greenie, neutral))
        this.findViewById<View>(R.id.graphSad).background = CustomBarDrawable(this, Emociones("Triste", pS, R.color.blue, sad))
        this.findViewById<View>(R.id.graphVerySad).background = CustomBarDrawable(this, Emociones("Muy triste", pVS, R.color.deepBlue, verySad))

        this.findViewById<LinearLayout>(R.id.graph).background = fondo
    }

    fun parseJson(jsonArray : JSONArray) : ArrayList<Emociones>{
        var lista = ArrayList<Emociones>()

        for (i in 0..jsonArray.length()) {
            try {
                val nombre = jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val total = jsonArray.getJSONObject(i).getDouble("total").toFloat()
                var emocion = Emociones(nombre, porcentaje, color, total)
                lista.add(emocion)
            } catch (e : JSONException) {
                e.printStackTrace()
            }
        }
        return lista
    }

    fun guardar(){
        var jsonArray = JSONArray()
        var o : Int = 0
        for (i in lista) {
            Log.d("OBJETOS", i.toString())
            var j : JSONObject = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color", i.color)
            j.put("total", i.total)

            jsonArray.put(o, j)
            o++
        }
        jsonFile?.saveData(this, jsonArray.toString())

        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }
}