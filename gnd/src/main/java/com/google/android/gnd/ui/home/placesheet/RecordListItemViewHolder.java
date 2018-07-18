/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gnd.ui.home.placesheet;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gnd.R;
import com.google.android.gnd.vo.Form;
import com.google.android.gnd.vo.Form.Element;
import com.google.android.gnd.vo.Form.Field;
import com.google.android.gnd.vo.Record;
import com.google.android.gnd.vo.Record.Value;
import java8.util.Optional;

class RecordListItemViewHolder extends RecyclerView.ViewHolder {
  private static final int MAX_SUMMARY_COLUMNS = 4;
  private final View view;
  private final MutableLiveData<Record> itemClicks;

  @BindView(R.id.field_label_row)
  TableRow fieldLabelRow;

  @BindView(R.id.field_value_row)
  TableRow fieldValueRow;

  public static RecordListItemViewHolder newInstance(
    ViewGroup parent, MutableLiveData<Record> itemClicks) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.record_list_item, parent, false);
    return new RecordListItemViewHolder(view, itemClicks);
  }

  private RecordListItemViewHolder(View view, MutableLiveData<Record> itemClicks) {
    super(view);
    this.view = view;
    this.itemClicks = itemClicks;
    ButterKnife.bind(this, view);
  }

  void update(Record record) {
    fieldLabelRow.removeAllViews();
    fieldValueRow.removeAllViews();
    Form form = record.getForm();
    // TODO: Clean this up.
    for (int i = 0; i < MAX_SUMMARY_COLUMNS && i < form.getElements().size(); i++) {
      Element elem = form.getElements().get(i);
      switch (elem.getType()) {
        case FIELD:
          Field field = elem.getField();
          Optional<Value> value =
            Optional.ofNullable(record.getValueMap().get(field.getId()));
          fieldLabelRow.addView(newFieldTextView(field.getLabel()));
          fieldValueRow.addView(
              newFieldTextView(value.map(v -> v.getSummaryText()).orElse("")));
          break;
      }
    }
    view.setOnClickListener(__ -> itemClicks.setValue(record));
  }

  @NonNull
  private TextView newFieldTextView(String text) {
    Context context = view.getContext();
    Resources resources = context.getResources();
    TextView v = new TextView(context);
    v.setTextAppearance(context, R.style.RecordListText_Field);
    // NOTE: These attributes don't work when applying text appearance programmatically, so we set
    // them here individually instead.
    v.setPadding(
        0, 0, resources.getDimensionPixelSize(R.dimen.record_summary_text_padding_right), 0);
    v.setMaxWidth(resources.getDimensionPixelSize(R.dimen.record_summary_text_max_width));
    v.setMaxLines(1);
    v.setSingleLine();
    v.setEllipsize(TruncateAt.END);
    v.setText(text);
    return v;
  }
}
