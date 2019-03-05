package com.example.whatisup.src.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.whatisup.R
import com.example.whatisup.src.data.model.Emoji
import com.example.whatisup.src.utils.getEmojiDrawable

class EmojiAdapter(private val emojiList: List<Emoji>, private val context: Context) : RecyclerView.Adapter<EmojiViewHolder>() {

    private val holderList: MutableList<EmojiViewHolder> = mutableListOf()
    var selected: Int = 2 // default selected is neutral

    override fun getItemCount(): Int {
        return emojiList.size
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.emojiIcon.setImageDrawable(getEmojiDrawable(position, context))

        if (position == selected) {
            holder.selectedIndicator.visibility = View.VISIBLE
        }

        holder.emojiIcon.setOnClickListener {
            hideAllSelectors()
            holder.selectedIndicator.visibility = View.VISIBLE
            selected = holder.adapterPosition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emoji_row_layout, parent, false)
        val holder = EmojiViewHolder(view)
        holderList.add(holder)
        return holder
    }

    private fun hideAllSelectors() {
        for (holder in holderList) {
            holder.selectedIndicator.visibility = View.INVISIBLE
        }
    }

    fun setSelection(type: Int) {
        hideAllSelectors()
        selected = type
        notifyDataSetChanged()
    }
}

class EmojiViewHolder(v: View) : RecyclerView.ViewHolder(v){
    val emojiIcon = v.findViewById<ImageButton>(R.id.emoticon_btn)
    val selectedIndicator = v.findViewById<View>(R.id.selected_indicator)
}