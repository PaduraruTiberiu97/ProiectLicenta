package com.tibep.proiectlicenta.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databasefavorite.FavoriteDatabase;
import com.tibep.proiectlicenta.ui.home.adapter.HomeAdapter;
import com.tibep.proiectlicenta.ui.home.adapter.HomeItems;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TextView nrOfItems;

    public static FavoriteDatabase favoriteDatabase;


    private RecyclerView homeRecyclerview;
    private String[] itemDescription = {" Modelul unisex de la RayBan nu face nicio diferenta intre femei si barbati.Ramele au un model aparte,fiind confectionate din metal rezistent de culoare neagra pentru o durabilitate sporita si pentru rezistenta impactului cu suprafete tari.", "Acest model care poartă semnătura Ray-Ban Junior este un accesoriu cu talente multiple, care face ca atât femeile cât şi bărbaţii să arăte super, în aceaşi măsură.", "Acest model de ochelari se adresează în mod special bărbaţilor trendy! Design-ul iesit din comun, cu linii clare, conferă o notă masculină discretă. Cu acest model de ochelari cu rame complete faci pe oricine să înţeleagă, fără dubii, că nu accepţi jumătăţiile de măsură! ", "Prin acest model de ochelari, designerii se adresează mai ales doamnelor, care se simt în largul lor în marile metropole ale lumii. Cine mai are timp să se gândească la „Mr. Right“? Acum este mai importantă stabilirea stilului perfect pentru 2019!Ochelarii fără rame se feresc să fie în „prim-plan”, însă reuşesc să puncteze tocmai prin această reţinere nobilă, care conferă look-ului o notă exclusivistă şi marcantă.", " Ochelarii cu formă pătrată accentuează şi mai mult forma chipului şi se dovedesc a fi un element de stil important pentru personalităţile pline de încredere în sine. Plasticul este un material extrem de uşor şi flexibil. Acest fapt conferă ramelor o durabilitate pe viaţă şi un confort maxim la purtare.", "Liniile pline de expresivitate conferă notei clasice a acestui model un caracter inconfundabil, ceea ce face din această pereche de ochelari un must-have inevitabil pentru orice femeie care se respectă. La noi, alături de estetică, un loc important este ocupat şi de funcţionalitate! Cu o protecţie 100% contra razelor UV a ochilor tăi, acum poate răsării şi soarele.", "Termenul de „ajutor vizual“ este total neinspirat pentru modelul LS109. Cu această pereche de ochelariai achiziţionat un accesoriu, care iţi va revoluţiona stilul personal, dovedind că ai un gust aparte în materie de modă! Modelul LS109 este lansat de curând pe piaţă în 2018, aşa încât cu siguranţă vei fi la ultimul răcnet cu aceşti ochelari. Modelul de ochelari LS109 este disponibil în shop-ul online Edel-Optics şi în alte variante şic de la Levis, din colecţiile 2017 şi 2018.", "Cu această piesă de designer îţi poţi considera ochii perfect „îmbrăcaţi” în fiecare zi. Cu acest nou model de la Carrera poţi demonstra că eşti un „trend-setter“ veritabil. Chiar şi în sezonul actual, acest brand reuşeşte să se impună prin colecţia sa, stabilind un trend deosebit pentru 2018.", "Necomplicaţi şi super calitativi datorită manoperei şi materialelor folosite, aceşti ochelari speciali pentru bărbaţi sunt un reper pentru design-ul elegant şi pentru cultivarea încrederii de sine.Ramele cu formă rectangulară emit un sentiment de putere şi de încredere totală de sine. În special în cazul feţelor cu formă rotundă, forma dreptunghiulară a ochelarilor creează un punct de contrast interesant."};
    private int[] imagesGif = {R.raw.glasses1gif, R.raw.glasses3gif, R.raw.glasses4gif, R.raw.glasses5gif,
            R.raw.glasses6gif, R.raw.glasses7gif, R.raw.glasses8gif, R.raw.glasses9gif, R.raw.glasses10gif};
    private int[] images3D = {R.raw.glasses1, R.raw.glasses3, R.raw.glasses4, R.raw.glasses5,
            R.raw.glasses6, R.raw.glasses7, R.raw.glasses8, R.raw.glasses9, R.raw.glasses10};
    private int[] images = {R.drawable.glasses1, R.drawable.glasses3, R.drawable.glasses4, R.drawable.glasses5,
            R.drawable.glasses6, R.drawable.glasses7, R.drawable.glasses8, R.drawable.glasses9, R.drawable.glasses10};
    private String[] glassesName = {"Ochelari RayBan", "Ochelari tip Aviator", "Cyber Glasses", "Ochelari Gucci", "Ochelari Puma", "Ochelari de soare", "Ochelari Levis", "Ochelari Carrera", "Ochelari Polo"};
    private int[] glassesPrice = {380, 410, 250, 290, 520, 600, 460, 330, 190};

    private ArrayList<HomeItems> homeItems = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        favoriteDatabase = Room.databaseBuilder(getActivity(), FavoriteDatabase.class, "myfavdb").allowMainThreadQueries().fallbackToDestructiveMigration().build(); //fallback-if we update the database we also need to provide a migration to the new database so all the data will remain saved.what fallback does is just deleting everythign from the database so we dont need to make a migration
        bindUI(root);

        buildHomeRecyclerView();


        return root;

    }

    private void bindUI(View root) {

        nrOfItems = root.findViewById(R.id.txt_nrItemsFound);
        nrOfItems.setText((images.length + " produse gasite"));

        homeRecyclerview = root.findViewById(R.id.rv_homeItems);
    }

    private void buildHomeRecyclerView() {

        homeItems.clear();

        for (int i = 0; i <= 8; i++) {
            homeItems.add(new HomeItems("Ochelari nr.x",
                    746, images[i], i + 1, images3D[i], imagesGif[i], itemDescription[i]));
        }

        homeRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        homeRecyclerview.setHasFixedSize(true);
        RecyclerView.Adapter homeAdapter = new HomeAdapter(homeItems);
        homeRecyclerview.setAdapter(homeAdapter);
    }
}