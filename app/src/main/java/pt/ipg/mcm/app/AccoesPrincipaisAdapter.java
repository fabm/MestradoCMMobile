package pt.ipg.mcm.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class AccoesPrincipaisAdapter extends ArrayAdapter<String> {
  private static List<String> values = Arrays.asList(
      "Sincronizar",
      "Criar encomenda",
      "Ver as minhas encomendas",
      "limpar dados"
  );

  public AccoesPrincipaisAdapter(Context context) {
    super(context, R.layout.item_accao_list, values);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) getContext()
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View accaoView = inflater.inflate(R.layout.item_accao_list, parent,false);

    TextView tvAccao = (TextView) accaoView.findViewById(R.id.ialTvAccao);

    tvAccao.setText(values.get(position));

    return accaoView;
  }

}
