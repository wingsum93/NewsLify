package com.crushtech.newslify.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.SavedArticlesAdapter
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class savedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: SavedArticlesAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel

        setUpRecyclerView()


        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)

                val snackListener = View.OnClickListener {
                    viewModel.saveArticle(article)
                }
                SimpleCustomSnackbar.make(
                    saved_coordinator, "article deleted",
                    Snackbar.LENGTH_LONG, snackListener, R.drawable.delete_article, "Undo",
                    ContextCompat.getColor(requireContext(), R.color.mygrey)
                )?.show()
            }

        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
            newsAdapter.differ.submitList(article)
            updateUI(article)
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = SavedArticlesAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun updateUI(article: List<Article>) {
        val adapterHasNoItem = article.isEmpty()
        if (adapterHasNoItem) {
            lottie_no_article_saved.visibility = View.VISIBLE
            no_saved_article_text1.visibility = View.VISIBLE
            no_saved_article_text2.visibility = View.VISIBLE
        } else {
            lottie_no_article_saved.visibility = View.INVISIBLE
            no_saved_article_text1.visibility = View.INVISIBLE
            no_saved_article_text2.visibility = View.INVISIBLE
        }
    }

    override fun onAttach(context: Context) {
        (activity as NewsActivity).showBottomNavigation()
        super.onAttach(context)
    }

    override fun onResume() {
        (activity as NewsActivity).showBottomNavigation()
        super.onResume()
    }


}

