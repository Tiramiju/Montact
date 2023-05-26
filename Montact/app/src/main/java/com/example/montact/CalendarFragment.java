package com.example.montact;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.montact.calendarDeco.EventDecorator;
import com.example.montact.calendarDeco.OneDayDecorator;
import com.example.montact.calendarDeco.SaturdayDecorator;
import com.example.montact.calendarDeco.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private OneDayDecorator oneDayDecorator;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<String> datesStr;
    private List<CalendarDay> dates = new ArrayList<>();
    private List<Contact> calendarContactList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread() {
            @Override
            public void run() {
                datesStr = ContactDB.getInstance(getContext()).contactDao().getDates();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                try {
                    for (String tmp : datesStr) {
                        Date date = simpleDateFormat.parse(tmp);
                        dates.add(CalendarDay.from(date));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar,container,false);

        Context context = view.getContext();
        oneDayDecorator = new OneDayDecorator(context);

        calendarView = view.findViewById(R.id.calendar_main_view);

        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator,
                new EventDecorator(Color.RED, dates));

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                String tmp = mFormat.format(date.getDate());

                new Thread() {
                    @Override
                    public void run() {
                        calendarContactList = ContactDB.getInstance(getContext()).contactDao().selectByDate(tmp);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contactAdapter.setList(calendarContactList);
                            }
                        });
                    }
                }.start();
            }
        });

        recyclerView = view.findViewById(R.id.calendar_rv);
        contactAdapter = new ContactAdapter(calendarContactList, getParentFragmentManager());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(contactAdapter);

        return view;
    }
}
