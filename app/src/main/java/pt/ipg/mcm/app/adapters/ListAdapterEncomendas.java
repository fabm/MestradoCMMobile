package pt.ipg.mcm.app.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.Encomenda;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.app.util.Formatter;
import pt.ipg.mcm.calls.client.DateHelper;

import java.util.List;

import static pt.ipg.mcm.app.util.Formatter.centsToEuros;

public class ListAdapterEncomendas extends BaseAdapter {

  private final Context context;
  protected List<Encomenda> data;

  public ListAdapterEncomendas(Context context) {
    SQLiteDatabase db = App.get().getOpenHelper(context).getReadableDatabase();
    DaoSession session = new DaoMaster(db).newSession();
    data = session.getEncomendaDao().loadAll();
    db.close();
    this.context = context;
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
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.item_encomenda, parent, false);
    }
    TextView data = (TextView) view.findViewById(R.id.leVlDataEntrega);
    TextView preco = (TextView) view.findViewById(R.id.leVlPrecoTotal);

    Encomenda item = (Encomenda) getItem(pos);


    data.setText(Formatter.dateSimpleToString(item.getDataEntrega()));
    preco.setText(centsToEuros(item.getPrecoTotal()));

    return view;
  }

}