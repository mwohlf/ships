package net.wohlfart.ships.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;


@Data
@RequiredArgsConstructor
public class TableNameList {

    final ArrayList<String> tableDetails = new ArrayList<>();

}
