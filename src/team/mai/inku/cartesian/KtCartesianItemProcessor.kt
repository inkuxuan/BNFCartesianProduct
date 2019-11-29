package team.mai.inku.cartesian

import team.mai.inku.cartesian.model.Item
import team.mai.inku.cartesian.model.OptionItem
import team.mai.inku.cartesian.model.SequenceItem
import team.mai.inku.cartesian.model.SimpleItem
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class KtCartesianItemProcessor {

    private val result: OptionItem = OptionItem()

    fun extractToOptionItem(source: Item): OptionItem {
        if(source == result){
            throw IllegalArgumentException("Do not pass in the same result object for another process!")
        }
        return when (source) {
            is SequenceItem ->
                processSequence(source)
            is OptionItem ->
                processPossibilities(source)
            is SimpleItem ->
                OptionItem(source)
            else -> throw IllegalArgumentException()
        }
    }

    private fun cartesianProduct(sequenceItem: SequenceItem, optionItem: OptionItem): OptionItem {
        val result = OptionItem();
        for (possibility in optionItem.options) {
            val list = ArrayList<Item>(sequenceItem.items)
            val copy = SequenceItem(list)
            copy.items.add(possibility)
            result.options.add(copy)
        }
        return result;
    }

    private fun cartesianProduct(optionItem: OptionItem, sequenceItem: SequenceItem): OptionItem {
        val result = OptionItem();
        for (possibility in optionItem.options) {
            val list = ArrayList<Item>(sequenceItem.items)
            val copy = SequenceItem(list)
            copy.items.add(0, possibility)
            result.options.add(copy)
        }
        return result;
    }

    private fun cartesianProduct(sequenceItem1: SequenceItem, optionItem: OptionItem, sequenceItem2: SequenceItem): OptionItem {
        val result = OptionItem();
        for (possibility in optionItem.options) {
            val list = ArrayList<Item>(sequenceItem1.items)
            val copy = SequenceItem(list)
            copy.items.add(possibility)
            copy.items.add(sequenceItem2)
            result.options.add(copy)
        }
        return result;
    }

    private fun processSequence(sequenceItem: SequenceItem): OptionItem {
        var allSimple = true
        val simpleSequence = SequenceItem();
        var i = 0;
        loop@ while (i < sequenceItem.items.size) {

            when (val item = sequenceItem.items[i]) {
                is SimpleItem -> simpleSequence.items.add(item)
                is OptionItem -> {
                    val remaining = SequenceItem(sequenceItem.items.subList(i + 1, sequenceItem.items.size))
                    val cartesianProduct = cartesianProduct(simpleSequence, item, remaining)
                    if(cartesianProduct.options == result.options){
                        System.err.println("Object reference same!!!")
                        System.err.println("run: cartesianProduct(")
                        System.err.println("$simpleSequence")
                        System.err.println("$item")
                        System.err.println("$remaining")
                    }
                    processPossibilities(cartesianProduct)
                    allSimple = false
                    break@loop
                }
                // nested
                is SequenceItem -> {
                    sequenceItem.items.removeAt(i)
                    for ((j, nestedItem) in item.items.withIndex()) {
                        sequenceItem.items.add(i + j, nestedItem)
                    }
                    i--
                }
            }
            i++
        }
        // all simple items
        if (allSimple) {
            result.options.add(sequenceItem)
        }
        return result;
    }

    private fun processPossibilities(optionItem: OptionItem): OptionItem {
        if(optionItem.options == result.options){
            throw IllegalStateException("")
        }
        var i = 0;
        while (i < optionItem.options.size) {
            val item = optionItem.options[i]
            if (item is SequenceItem) {
                processSequence(item)
            }
            // nested, will process when it comes to the actual item
            else if (item is OptionItem) {
                optionItem.options.removeAt(i)
                for (nestedItem in item.options) {
                    optionItem.options.add(nestedItem)
                }
                i--
            }
            else if (item is SimpleItem){
                result.options.add(item);
            }
            i++
        }
        return result
    }

}