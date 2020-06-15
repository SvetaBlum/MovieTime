package com.example.movietowatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movietowatch.adapters.MovieAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CoordinatorLayout coordinatorLayout;
    MovieAdapter adapter;
    RecyclerView recyclerView;

    String fullName;

    GoogleSignInClient mGoogleSignInClient;
    SignInButton mGoogleLoginBtn;
    private static final int RC_SIGN_IN=100;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseAuth.AuthStateListener authStateListener;       ////lisen to chenges in/out

  //  FirebaseDatabase database = FirebaseDatabase.getInstance();
  //  DatabaseReference users = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        coordinatorLayout = findViewById(R.id.coordinator);

        //recyclerView of popular movies
        recyclerView = findViewById(R.id.recycler_popular);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MoviesViewModel model = ViewModelProviders.of(MainActivity.this).get(MoviesViewModel.class);
        model.getMovies().observe(MainActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movieList) {
                adapter = new MovieAdapter(MainActivity.this, movieList);
                recyclerView.setAdapter(adapter);
            }
        });



        //toolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //google log in
        mGoogleLoginBtn = findViewById(R.id.google_in);
        GoogleSignInOptions gos = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gos);

        //navigaton view
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.sign_dialog, null);

                final EditText usernameEt = dialogView.findViewById(R.id.username_input);
                final EditText fullnameEt = dialogView.findViewById(R.id.fullname_input);
                final EditText passwordEt = dialogView.findViewById(R.id.password_input);


                switch (item.getItemId()) {

                    case R.id.item_sign_up:

                       /* if(mGoogleLoginBtn.isPressed()){
                            Snackbar.make(coordinatorLayout, "eeeeshhhhh", Snackbar.LENGTH_SHORT).show();
                            mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                                    startActivityForResult(signInIntent, RC_SIGN_IN);
                                }
                            });*/
                       // }else
                        builder.setView(dialogView).setPositiveButton("Register", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String username = usernameEt.getText().toString();
                                fullName = fullnameEt.getText().toString();
                                String password = passwordEt.getText().toString();

                                //sign up the user
                                firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful())
                                            Snackbar.make(coordinatorLayout, "Sign up successful", Snackbar.LENGTH_SHORT).show();
                                        else
                                            Snackbar.make(coordinatorLayout, "Sign up failed", Snackbar.LENGTH_SHORT).show();

                                    }
                                });


                            }
                        }).show();

                        break;
                    case R.id.item_sign_in:

                        fullnameEt.setVisibility(View.GONE);
                        builder.setView(dialogView).setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String username = usernameEt.getText().toString();
                                String password = passwordEt.getText().toString();

                                //Sign in the user

                                firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful())
                                            Snackbar.make(coordinatorLayout, "Sign in successful", Snackbar.LENGTH_SHORT).show();
                                        else
                                            Snackbar.make(coordinatorLayout, "Sign in failed", Snackbar.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        }).show();

                        break;
                    case R.id.item_sign_out:
                        firebaseAuth.signOut();
                        break;

                    case R.id.item_profile_menu:
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this,Profile.class);
                      // intent.putExtra("UserName",usernameEt.getText());
                        startActivity(intent);
                        break;

                    case R.id.item_home:
                        drawerLayout.closeDrawers();

                        break;





                }
                return false;
            }
        });

       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView  = getLayoutInflater().inflate(R.layout.mission_dialog,null);

                final EditText editText = dialogView.findViewById(R.id.mission_txt);
                builder.setView(dialogView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = editText.getText().toString();
                        missions.add(new Mission(text,false));
                        adapter.notifyItemInserted(missions.size()-1);

                        //update the database
                        users.child(firebaseAuth.getCurrentUser().getUid()).setValue(missions);

                        Snackbar.make(coordinatorLayout,"Mission added",Snackbar.LENGTH_LONG).show();
                    }
                }).show();
            }
                Toast.makeText(MainActivity.this, "fab pressed", Toast.LENGTH_SHORT).show();
            }
        });*/
       //up popcorn
        final CollapsingToolbarLayout collapsing = findViewById(R.id.collapsing_layout);
        collapsing.setTitle("Please log in");
        collapsing.setExpandedTitleColor(getResources().getColor(R.color.colorWh));

        //
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {       ////calls at sign in/out

                View headerView  = navigationView.getHeaderView(0);
                TextView userTv = headerView.findViewById(R.id.navigation_header_text_view);

                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {//sign up or sign in

                    if(fullName!=null)  { //sign up - update profile with full name

                        user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(fullName).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                fullName = null;
                                if(task.isSuccessful())
                                    Snackbar.make(coordinatorLayout,user.getDisplayName() + " Welcome!!!",Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                    userTv.setText(user.getDisplayName() + " logged in");              ///doing at both cases
                  collapsing.setTitle(user.getDisplayName());
                  collapsing.setExpandedTitleColor(getResources().getColor(R.color.colorWh));


                    navigationView.getMenu().findItem(R.id.item_sign_in).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_sign_up).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_sign_out).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_profile_menu).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_home).setVisible(true);

                    //Read the user data base - missions

                   /* final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loading " + user.getDisplayName() + " missions, please wait...");
                    progressDialog.show();

                    users.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                          //  missions.clear();

                            if(dataSnapshot.exists()) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Mission mission = snapshot.getValue(Mission.class);
                                   missions.add(mission);
                                }
                               adapter.notifyDataSetChanged();
                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/

                }
                else {
                    userTv.setText("Please log in");
                    collapsing.setTitle("Please log in");

                    navigationView.getMenu().findItem(R.id.item_sign_in).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_sign_up).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_sign_out).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_profile_menu).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_home).setVisible(false);

                 //   missions.clear();
                  //  adapter.notifyDataSetChanged();
                }
            }
        };

    }




    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Snackbar.make(coordinatorLayout,"Home button pressed",Snackbar.LENGTH_LONG).show();
            //  Toast.makeText(this, "Home button pressed", Toast.LENGTH_SHORT).show();
               drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Snackbar.make(coordinatorLayout,"Google sign in failed"+e,Snackbar.LENGTH_LONG).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Snackbar.make(coordinatorLayout,"signInWithCredential:success",Snackbar.LENGTH_LONG).show();
                            FirebaseUser user =  firebaseAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this,Profile.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(coordinatorLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(coordinatorLayout,"Google sign in failed"+e,Snackbar.LENGTH_LONG).show();
            }
        });
    }
}