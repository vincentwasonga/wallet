/*
 * ﻿Copyright [2016] [Peter Vincent]
 * Licensed under the Apache License, Version 2.0 (Personal Budget);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.octopus.budget.models.adapters;


import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.octopus.budget.R;
import com.octopus.budget.models.DescriptionSet;
import com.octopus.budget.models.Record;
import com.octopus.budget.models.math.HelperFunc;
import com.octopus.budget.models.notification.Notification;

import java.util.ArrayList;

/**
 * Created by octopus on 6/28/16.
 */
public class ClassifiedRecordsRecyclerAdapter extends RecyclerView.Adapter<ClassifiedRecordsRecyclerAdapter.ViewHolder> {
    private Notification notification;
    private ArrayList<Record> records;
    private int green;
    private int orange;
    private int light_blue;
    private ViewHolder holder;
    private View v;

    private Drawable salaryId;
    private Drawable carId;
    private Drawable groceriesId;
    private Drawable eatingoutId;
    private Drawable transportId;
    private Drawable entertainmentId;
    private Drawable wardrobeId;
    private Drawable personalId;
    private Drawable drugsId;
    private Drawable householdId;
    private Drawable electronicsId;
    private Drawable rentId;
    private Drawable vacationId;
    private Drawable feeId;
    private Drawable defaultId;
    private Drawable phoneId;
    private Drawable mpesaId;

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getOrange() {
        return orange;
    }

    public void setOrange(int orange) {
        this.orange = orange;
    }

    public ViewHolder getHolder() {
        return holder;
    }

    public void setHolder(ViewHolder holder) {
        this.holder = holder;
    }

    public View getV() {
        return v;
    }

    public void setV(View v) {
        this.v = v;
    }

    public Drawable getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(Drawable salaryId) {
        this.salaryId = salaryId;
    }

    public Drawable getCarId() {
        return carId;
    }

    public void setCarId(Drawable carId) {
        this.carId = carId;
    }

    public Drawable getGroceriesId() {
        return groceriesId;
    }

    public void setGroceriesId(Drawable groceriesId) {
        this.groceriesId = groceriesId;
    }

    public Drawable getEatingoutId() {
        return eatingoutId;
    }

    public void setEatingoutId(Drawable eatingoutId) {
        this.eatingoutId = eatingoutId;
    }

    public Drawable getTransportId() {
        return transportId;
    }

    public void setTransportId(Drawable transportId) {
        this.transportId = transportId;
    }

    public Drawable getEntertainmentId() {
        return entertainmentId;
    }

    public void setEntertainmentId(Drawable entertainmentId) {
        this.entertainmentId = entertainmentId;
    }

    public Drawable getWardrobeId() {
        return wardrobeId;
    }

    public void setWardrobeId(Drawable wardrobeId) {
        this.wardrobeId = wardrobeId;
    }

    public Drawable getPersonalId() {
        return personalId;
    }

    public void setPersonalId(Drawable personalId) {
        this.personalId = personalId;
    }

    public Drawable getDrugsId() {
        return drugsId;
    }

    public void setDrugsId(Drawable drugsId) {
        this.drugsId = drugsId;
    }

    public Drawable getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Drawable householdId) {
        this.householdId = householdId;
    }

    public Drawable getElectronicsId() {
        return electronicsId;
    }

    public void setElectronicsId(Drawable electronicsId) {
        this.electronicsId = electronicsId;
    }

    public Drawable getRentId() {
        return rentId;
    }

    public void setRentId(Drawable rentId) {
        this.rentId = rentId;
    }

    public Drawable getVacationId() {
        return vacationId;
    }

    public void setVacationId(Drawable vacationId) {
        this.vacationId = vacationId;
    }

    public Drawable getFeeId() {
        return feeId;
    }

    public void setFeeId(Drawable feeId) {
        this.feeId = feeId;
    }

    public Drawable getDefaultId() {
        return defaultId;
    }

    public void setDefaultId(Drawable defaultId) {
        this.defaultId = defaultId;
    }

    public Drawable getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Drawable phoneId) {
        this.phoneId = phoneId;
    }

    public Drawable getMpesaId() {
        return mpesaId;
    }

    public void setMpesaId(Drawable mpesaId) {
        this.mpesaId = mpesaId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public TextView item;
        public TextView amount;
        public TextView date;
        public TextView location;
        public TextView description;
        public View view;


        public ViewHolder(View v) {
            super(v);
            item = (TextView) v.findViewById(R.id.item);
            amount = (TextView) v.findViewById(R.id.amount);
            date = (TextView) v.findViewById(R.id.date);
            imageView = (ImageView) v.findViewById(R.id.list_image);
            location = (TextView) v.findViewById(R.id.locationTextView);
            description = (TextView) v.findViewById(R.id.descriptionTextView);
            setGreen(v.getResources().getColor(R.color.colorGreen));
            setOrange(v.getResources().getColor(R.color.colorOrange));
            light_blue = v.getResources().getColor(R.color.colorBlue_Light);
            view = v;
            setSalaryId(view.getResources().getDrawable(R.drawable.salary_icon));
            setCarId(view.getResources().getDrawable(R.drawable.car_icon));
            setGroceriesId(view.getResources().getDrawable(R.drawable.groceries_icon));
            setEatingoutId(view.getResources().getDrawable(R.drawable.eating_out_icon));
            setTransportId(view.getResources().getDrawable(R.drawable.transport_icon));
            setEntertainmentId(view.getResources().getDrawable(R.drawable.entertainment_icon));
            setWardrobeId(view.getResources().getDrawable(R.drawable.wardrobe_icon));
            setPersonalId(view.getResources().getDrawable(R.drawable.personal_icon));
            setDrugsId(view.getResources().getDrawable(R.drawable.drugs_alcohol_icon));
            setHouseholdId(view.getResources().getDrawable(R.drawable.household_icon));
            setElectronicsId(view.getResources().getDrawable(R.drawable.electronic_icon));
            setRentId(view.getResources().getDrawable(R.drawable.rent_icon));
            setVacationId(view.getResources().getDrawable(R.drawable.vacation_icon));
            setFeeId(view.getResources().getDrawable(R.drawable.fee_icon));
            setPhoneId(view.getResources().getDrawable(R.drawable.phone_icon));
            setMpesaId(view.getResources().getDrawable(R.drawable.mpesa_icon));
            setDefaultId(view.getResources().getDrawable(R.drawable.default_money));
        }
    }

    public void add(int position, Record item) {
        getRecords().add(position, item);
        notifyItemInserted(position);

    }

    public void remove(Record item) {
        int position = getRecords().indexOf(item);
        getRecords().remove(position);
        notifyItemRemoved(position);
    }

    public ClassifiedRecordsRecyclerAdapter(ArrayList<Record> myDataset) {
        setRecords(myDataset);
        setNotification(new Notification(null));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        setV(LayoutInflater.from(parent.getContext()).inflate(R.layout.record_row, parent, false));
        // set the view's size, margins, paddings and layout parameters
        setHolder(new ViewHolder(getV()));
        return getHolder();
    }

    private final static int FADE_DURATION = 1000; // in milliseconds

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Record r = getRecords().get(position);
        if (r.isGained()) {
            viewHolder.item.setTextColor(getGreen());
        }
        else if (r.isLost()) {
            viewHolder.item.setTextColor(getOrange());
        }
        viewHolder.item.setText(r.getNameItem());
        viewHolder.amount.setText(HelperFunc.getMoney(r.getAmount()));
        if (r.getType().equals(Record.Income)) {
            viewHolder.amount.setTextColor(getGreen());
        }
        else if (r.getType().equals(Record.Expense)) {
            viewHolder.amount.setTextColor(getOrange());
        }

        viewHolder.imageView.setImageDrawable(getIcon(r.getNameItem()));

        viewHolder.date.setText(r.getDateString());
        if (r.getDescriptionSet().getDescription().equals(DescriptionSet.DEFAULT)) {
            viewHolder.description.setVisibility(View.GONE);
        }
        else {
            viewHolder.description.setText(r.getDescriptionSet().getDescription());
        }
        if (r.getDescriptionSet().getLocationName().equals(DescriptionSet.DEFAULT)) {
            viewHolder.location.setVisibility(View.GONE);
        }
        else {
            viewHolder.location.setText(r.getDescriptionSet().getLocationName());
        }
        if (position % 2 == 1) {
            viewHolder.view.setBackgroundColor(light_blue);
        }
        doScaleAnimation(viewHolder.view);

    }
    private void doScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
    private Drawable getIcon(String name) {
        Drawable resid = null;
        switch (name) {
            case "Salary": {
                resid = getSalaryId();
                break;
            }
            case "Car": {
                resid = getCarId();
                break;
            }
            case "Groceries": {
                resid = getGroceriesId();
                break;
            }
            case "Eating out": {
                resid = getEatingoutId();
                break;
            }
            case "Transport": {
                resid = getTransportId();
                break;
            }
            case "Entertainment": {
                resid = getEntertainmentId();
                break;
            }
            case "Wardrobe": {
                resid = getWardrobeId();
                break;
            }
            case "Personal": {
                resid = getPersonalId();
                break;
            }
            case "Drugs and Alcohol": {
                resid = getDrugsId();
                break;
            }
            case "Household": {
                resid = getHouseholdId();
                break;
            }
            case "Electronics": {
                resid = getElectronicsId();
                break;
            }
            case "Rent": {
                resid = getRentId();
                break;
            }
            case "Vacation": {
                resid = getVacationId();
                break;
            }
            case "Fee": {
                resid = getFeeId();
                break;
            }
            case "Phone and Internet": {
                resid = getPhoneId();
                break;
            }
            case "MPESA": {
                resid = getMpesaId();
                break;
            }
            default: {
                resid = getDefaultId();
                break;
            }
        }
        return resid;
    }


    @Override
    public int getItemCount() {
        return getRecords().size();
    }

    public boolean assertEquals(String a, String b) {
        return a.equalsIgnoreCase(b);
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

}
