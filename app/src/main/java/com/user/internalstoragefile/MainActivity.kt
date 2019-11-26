package com.user.internalstoragefile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.StringBuilder

private const val FILENAME = "testfile.txt"
private const val FILEPATH = "myFiles"

class MainActivity : AppCompatActivity() {

    private val PERMISSION_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun readFile(view: View){
        val stringBuilder = StringBuilder()
        try {
            val inputStream = openFileInput(FILENAME)
            if (inputStream != null){
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var newLine: String?
                newLine = bufferedReader.readLine()
                while (newLine != null){
                    stringBuilder.append(newLine)
                    newLine = bufferedReader.readLine()
                }
                inputStream.close()
            }
        } catch (e: IOException){
            e.printStackTrace()
        }
        editText.text.append(stringBuilder)
    }

    fun writeFile(view: View){
        try {
            val fileOutputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE)
            fileOutputStream.write(editText.text.toString().toByteArray())
            fileOutputStream.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun writeFileExternal(view: View){
        if (isExternalStorageWritable()){
            checkStoragePermission()
            try {
                val textFile = File(getExternalFilesDir(FILEPATH), FILENAME)
                val fileOutputStream = FileOutputStream(textFile)
                fileOutputStream.write(editText.text.toString().toByteArray())
                fileOutputStream.close()
            } catch (e: IOException){
                e.printStackTrace()
                Toast.makeText(this, "Error writing file",
                    Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Cannot write to External Storage",
                Toast.LENGTH_LONG).show();
        }
    }

    fun readFileExternal(view: View){
        if (isExternalStorageReadable()){
            checkStoragePermission()
            val stringBuilder = StringBuilder()
            try {
                val textFile = File(getExternalFilesDir(FILEPATH), FILENAME)
                val fileInputStream: FileInputStream? = FileInputStream(textFile)
                if (fileInputStream != null){
                    val inputStreamReader = InputStreamReader(fileInputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var newLine: String?
                    newLine = bufferedReader.readLine()
                    while (newLine != null){
                        stringBuilder.append(newLine)
                        newLine = bufferedReader.readLine()
                    }
                    fileInputStream.close()
                }
            } catch (e: IOException){
                e.printStackTrace()
                Toast.makeText(this, "Error reading file",
                    Toast.LENGTH_LONG).show()
            }
            editText.text.append(stringBuilder)
        } else {
            Toast.makeText(this, "Cannot read External Storage",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun isExternalStorageWritable(): Boolean{
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()){
            return true
        }
        return false
    }

    private fun isExternalStorageReadable(): Boolean{
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() ||
                Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()){
            return true
        }
        return false
    }

    private fun checkStoragePermission(){
        val permission = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, 101)
        }
    }
}
