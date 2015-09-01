package pt.ipg.mcm.app.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Filter;
import pt.ipg.mcm.calls.client.model.localidades.LocalidadeRest;

import java.util.ArrayList;
import java.util.List;

public class LocalidadesFilter extends Filter implements TextWatcher {

  private List<LocalidadeRest> localidades;

  public LocalidadesFilter(List<LocalidadeRest> localidades) {
    this.localidades = localidades;
  }

  @Override
  protected FilterResults performFiltering(CharSequence constraint) {
    String filteredString = constraint.toString().toUpperCase();
    FilterResults filterResults = new FilterResults();

    List<LocalidadeRest> fList = new ArrayList<LocalidadeRest>(localidades.size());

    for (int i = 0; i < localidades.size(); i++) {
      LocalidadeRest localidade = localidades.get(i);
      if (localidade.getLocalidade().toUpperCase().contains(filteredString)) {
        fList.add(localidade);
      }
    }
    filterResults.count = fList.size();
    filterResults.values = fList;
    return filterResults;
  }

  @Override
  protected void publishResults(CharSequence constraint, FilterResults results) {
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
  }

  @Override
  public void afterTextChanged(Editable s) {
    filter(s.toString());
  }
}
