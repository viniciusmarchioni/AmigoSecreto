package com.example.amigosecretoguest

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.ak1.BubbleTabBar
import io.ak1.OnBubbleClickListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFragment(Home())

        val bubbleTabBar = findViewById<BubbleTabBar>(R.id.bubbleTabBar)

        bubbleTabBar.addBubbleListener {

            when (it) {

                R.id.home -> {
                    if (supportFragmentManager.findFragmentById(R.id.frame) !is Home) {
                        supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(
                                R.anim.entry_left_to_right,
                                R.anim.exit_left_to_right
                            )
                            .replace(R.id.frame, Home())
                            .commit()
                    }
                }

                R.id.criar -> { //gerenciar
                    if (supportFragmentManager.findFragmentById(R.id.frame) !is CreateSession) {
                        if (supportFragmentManager.findFragmentById(R.id.frame) is Home) {
                            supportFragmentManager
                                .beginTransaction()
                                .setCustomAnimations(
                                    R.anim.entry_right_to_left,
                                    R.anim.exit_right_to_left
                                )
                                .replace(R.id.frame, CreateSession())
                                .commit()
                        } else {
                            supportFragmentManager
                                .beginTransaction()
                                .setCustomAnimations(
                                    R.anim.entry_left_to_right,
                                    R.anim.exit_left_to_right
                                )
                                .replace(R.id.frame, CreateSession())
                                .commit()
                        }
                    }
                }

                R.id.gerenciar -> { // criar
                    if (supportFragmentManager.findFragmentById(R.id.frame) !is ManagementSession) {
                        if (supportFragmentManager.findFragmentById(R.id.frame) is HelpFragment) {
                            supportFragmentManager
                                .beginTransaction()
                                .setCustomAnimations(
                                    R.anim.entry_left_to_right,
                                    R.anim.exit_left_to_right
                                )
                                .replace(R.id.frame, ManagementSession())
                                .commit()
                        } else {
                            supportFragmentManager
                                .beginTransaction()
                                .setCustomAnimations(
                                    R.anim.entry_right_to_left,
                                    R.anim.exit_right_to_left
                                )
                                .replace(R.id.frame, ManagementSession())
                                .commit()
                        }
                    }
                }

                R.id.help -> {//ajuda
                    if (supportFragmentManager.findFragmentById(R.id.frame) !is HelpFragment) {
                        supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(
                                R.anim.entry_right_to_left,
                                R.anim.exit_right_to_left
                            )
                            .replace(R.id.frame, HelpFragment())
                            .commit()
                    }


                }


            }


        }

    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }

}

