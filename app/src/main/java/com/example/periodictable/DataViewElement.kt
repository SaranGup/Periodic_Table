@file:Suppress("unused")

package com.example.periodictable

import android.content.res.Resources
import org.w3c.dom.Document
import java.io.InputStream
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class ElementData
object InfoList {

    private var loaded = false

    private var elementPropertyName: MutableList<String> = mutableListOf()
    val propertyName: MutableList<String> get() {return elementPropertyName}

    data class ElementDataList (
        var property: MutableList<String>
    ) {
        fun property(key: String): String
        {
            return property[elementPropertyName.indexOf(key)]
        }
    }

    private var elementProperties = mutableListOf<ElementDataList>()
    val elements: MutableList<ElementDataList> get() {return elementProperties}

    fun loadElements(assentName: String, resources: Resources) {

        if(loaded) return
        loaded = true

        val istream: InputStream = resources.assets.open(assentName)
        val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder: DocumentBuilder = builderFactory.newDocumentBuilder()
        val doc: Document = docBuilder.parse(istream)

        val tags = doc.getElementsByTagName("tag")
        for(i in 0 until tags.length){
            val tag = tags.item(i).firstChild.nodeValue
            elementPropertyName.add(tag)
        }

        for(i in 0 until doc.getElementsByTagName("element").length) elementProperties.add(ElementDataList(mutableListOf()))

        for (i in 0 until elementProperties.size)
        {
            elementProperties[i].property.add((i+1).toString())
        }

        for(name in elementPropertyName){
            val properties = doc.getElementsByTagName(name)
            for (i in 0 until elementProperties.size)
            {
                elementProperties[i].property.add(properties.item(i).firstChild.nodeValue)
            }
        }

        for(i in 0 until elementPropertyName.size) {
            elementPropertyName[i] = elementPropertyName[i].replace('_',' ')
            elementPropertyName[i] = elementPropertyName[i].split(" ").joinToString(" ") { it ->
                it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            }
        }
        elementPropertyName.add(0, "Atomic Number")
    }
}