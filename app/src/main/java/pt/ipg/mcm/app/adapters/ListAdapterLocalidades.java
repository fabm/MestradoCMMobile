package pt.ipg.mcm.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import pt.ipg.mcm.calls.client.model.localidades.LocalidadeRest;

import java.util.List;

public class ListAdapterLocalidades extends BaseAdapter {

  private final Context context;
  protected List<LocalidadeRest> data;

  public ListAdapterLocalidades(List<LocalidadeRest> data, Context context) {
    this.data = data;

    this.context = context;
  }

  public void updateData(List<LocalidadeRest> localidadeList) {
    data = localidadeList;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return data.size();
  }

  @Override
  public Object getItem(int position) {
    return data.get(position);
  }

  @Override
  public long getItemId(int id) {
    return id;
  }

  @Override
  public View getView(int pos, View view, ViewGroup parent) {

    if (view == null) {
      //We must create a View:
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
    }
    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

    LocalidadeRest item = (LocalidadeRest) getItem(pos);

    String codPostalString = String.valueOf(item.getCodPostal());

    text2.setText(codPostalString.substring(0, 4) + "-" + codPostalString.substring(4));
    text1.setText(item.getLocalidade());

    return view;
  }


  public void addData(List<LocalidadeRest> localidades) {
    data.addAll(localidades);
    notifyDataSetChanged();
  }
}