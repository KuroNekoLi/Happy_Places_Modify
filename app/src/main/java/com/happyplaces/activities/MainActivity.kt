package com.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.happyplaces.App
import com.happyplaces.HappyPlaceAdapter
import com.happyplaces.databinding.ActivityMainBinding
import com.happyplaces.presentation.di.HappyPlaceViewModel
import com.happyplaces.util.SwipeToDeleteCallback
import com.happyplaces.util.SwipeToEditCallback

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HappyPlaceViewModel

    private val factory by lazy { App.instance.factory }
    private lateinit var myAdapter: HappyPlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        myAdapter = HappyPlaceAdapter()
        binding.rvHappyPlace.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Toast.makeText(applicationContext, "edit", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, HappyPlaceDetailActivity::class.java))
                myAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvHappyPlace)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Toast.makeText(applicationContext, "delete", Toast.LENGTH_SHORT).show()
                val itemToDelete = myAdapter.getHappyPlaceAt(viewHolder.adapterPosition)
                viewModel.delete(itemToDelete)
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.rvHappyPlace)

        viewModel = ViewModelProvider(this, factory)[HappyPlaceViewModel::class.java]
        viewModel.apply {
            message.observe(this@MainActivity) {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
            getDataList().observe(this@MainActivity) {
                myAdapter.submitList(it)
            }
        }
        setContentView(binding.root)

        binding.fabAddHappyPlace.setOnClickListener {
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
        }
    }
}
