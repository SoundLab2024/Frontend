package view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.soundlab.R;
import model.UserStats;
import view.CustomButton;

import java.util.ArrayList;
import java.util.List;

public class AnaliticheFragment extends Fragment {

    private List<UserStats> userStatsList;
    private boolean isTableVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inizializzazione della lista di UserStats con dati fittizi
        userStatsList = new ArrayList<>();
        userStatsList.add(new UserStats("Utente1", "Mattina", 5));
        userStatsList.add(new UserStats("Utente2", "Pomeriggio", 8));
        userStatsList.add(new UserStats("Utente3", "Sera", 12));

        View view = inflater.inflate(R.layout.fragment_analitiche, container, false);

        // Assicurati che nel tuo layout ci sia un elemento con l'id analiticheButton
        CustomButton analiticheButton = view.findViewById(R.id.casuale); // Assicurati che questo ID corrisponda a quello nel tuo layout XML
        if (analiticheButton != null) {
            analiticheButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Alterna tra mostrare e nascondere la tabella ad ogni clic
                    isTableVisible = !isTableVisible;
                    if (isTableVisible) {
                        displayUserStats();
                    } else {
                        hideUserStats();
                    }
                }
            });
        }

        return view;
    }

    private void displayUserStats() {
        TableLayout tableLayout = requireView().findViewById(R.id.tableLayout);

        if (tableLayout != null) {
            tableLayout.setVisibility(View.VISIBLE); // Assicurati che la tabella sia visibile

            tableLayout.removeAllViews();

            for (UserStats userStats : userStatsList) {
                TableRow tableRow = new TableRow(requireContext());
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView tvUsername = new TextView(requireContext());
                tvUsername.setText(userStats.getUsername());
                tableRow.addView(tvUsername);

                TextView tvPreferredTime = new TextView(requireContext());
                tvPreferredTime.setText(userStats.getPreferredTime());
                tableRow.addView(tvPreferredTime);

                TextView tvTotalListens = new TextView(requireContext());
                tvTotalListens.setText(String.valueOf(userStats.getTotalListens()));
                tableRow.addView(tvTotalListens);

                tableLayout.addView(tableRow);
            }
        }
    }

    private void hideUserStats() {
        // Trova il TableLayout nel tuo layout XML
        TableLayout tableLayout = requireView().findViewById(R.id.tableLayout);
        if (tableLayout != null) {
            // Nascondi la tabella
            tableLayout.setVisibility(View.GONE);
        }
    }

    }