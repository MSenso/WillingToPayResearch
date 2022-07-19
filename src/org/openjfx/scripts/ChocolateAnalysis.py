import csv
import re

import numpy as np
import pandas as pd
from statistics import stdev


def get_choco_order(path_to_volunteer_data):
    return ''.join(open(path_to_volunteer_data + '/choco_order.txt', 'r', encoding='utf8')
                   .readlines()).replace('\t', '').split('\n')


def combine_path(path_to_database_folder, vol_name):
    if path_to_database_folder == '':
        return vol_name
    else:
        return path_to_database_folder + '/' + vol_name


"""
ЗДЕСЬ БУДЕМ ФОРМИРОВАТЬ 3 ФАЙЛА С ИНФОРМАЦИЕЙ о:
- готовности платить за каждый из образцов шоколада в слепой дегустации
- оценках стоймости образца в слепой дегустации
- вкусовых оценках образца в слепой дегустации
"""


def import_taste_data(vol_names, path_to_database_folder):
    string_structure = {
        "AG": [0, 1],
        "Lindt": [2, 3],
        "Kremlin": [4, 5],
        "Fructose": [6, 7],
        "Carob": [8, 9]
    }

    first_line = "AG1; AG2; L1; L2; Kremlin1; Kremlin2; FR1; FR2; Carob1; Carob2\n"
    for vol_name in vol_names:

        path_to_volunteer_data = combine_path(path_to_database_folder, vol_name)

        WTP_array = ['NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA']
        Taste_array = ['NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA']
        Price_array = ['NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA']

        text_file = open(path_to_volunteer_data + "/taste.csv", "w")
        text_file.write(first_line)
        text_file.close()

        text_file = open(path_to_volunteer_data + "/WTP.csv", "w")
        text_file.write(first_line)
        text_file.close()

        text_file = open(path_to_volunteer_data + "/price.csv", "w")
        text_file.write(first_line)
        text_file.close()

        file1 = open(path_to_volunteer_data + '/first.txt', 'r', encoding='utf8')
        file1.readline()

        Lines_about_order = get_choco_order(path_to_volunteer_data)

        # Strips the newline character
        for line in Lines_about_order:
            current_chocolate = line.strip()
            # Проверяем в какой раз видим данный шоколад
            if WTP_array[string_structure[current_chocolate][0]] == 'NA':
                file1.readline()
                Taste_array[string_structure[current_chocolate][0]] = (file1.readline()[:-1])
                file1.readline()
                WTP_array[string_structure[current_chocolate][0]] = (file1.readline()[:-2])
                file1.readline()
                Price_array[string_structure[current_chocolate][0]] = (file1.readline()[:-2])
            else:
                file1.readline()
                Taste_array[string_structure[current_chocolate][1]] = (file1.readline()[:-1])
                file1.readline()
                WTP_array[string_structure[current_chocolate][1]] = (file1.readline()[:-2])
                file1.readline()
                Price_array[string_structure[current_chocolate][1]] = (file1.readline()[:-2])

        file1.close()

        line_WTP = ";".join(WTP_array) + '\n'
        line_Taste = ";".join(Taste_array) + '\n'
        line_Price = ";".join(Price_array) + '\n'

        text_file = open(path_to_volunteer_data + "/taste.csv", "a")
        text_file.write(line_Taste)
        text_file.close()

        text_file = open(path_to_volunteer_data + "/WTP.csv", "a")
        text_file.write(line_WTP)
        text_file.close()

        text_file = open(path_to_volunteer_data + "/price.csv", "a")
        text_file.write(line_Price)
        text_file.close()


def my_import(path_to_volunteer_data, sampling_freq=500, channel_number=21, start_time=0.0):
    # import EEG data

    data_raw = []
    data = {}

    for ch in range(channel_number):
        data['%d' % ch] = []

    for ch in range(channel_number):
        with open('%s/CSV_Export/EEG_%d.csv' % (path_to_volunteer_data, ch), 'rt') as csvfile:
            reader = csv.reader(csvfile, delimiter=';')
            data = next(reader)
            data_raw.append(data[int(start_time * sampling_freq):])
            data_raw[ch] = [w.replace(',', '.') for w in data_raw[ch]]
            data_raw[ch] = [float(w) / 1000000000 for w in data_raw[ch]]

    data_raw = np.array(data_raw)
    return data_raw


# Расчитывает Фурье-спектр для "куска" сигнала и заданной частоте дискретизации
def my_fft(signal, sample_rate=500):
    n = signal.size
    fft_value = np.abs(np.fft.rfft(signal)) ** 2
    freq = np.fft.rfftfreq(n, d=1. / sample_rate)
    return fft_value, freq


def initialize_wave_structure(names):
    struct = {}
    for name in names:
        struct[name] = ['NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA']
    return struct


def get_ind_by_alpha(freqs):
    return [ind for ind in range(freqs.size) if (8 <= freqs[ind] <= 13)]


def get_ind_by_beta(freqs):
    return [ind for ind in range(freqs.size) if (13 <= freqs[ind] <= 30)]


def get_ind_by_gamma(freqs):
    return [ind for ind in range(freqs.size) if (30 <= freqs[ind] <= 60)]


def get_ind_by_delta(freqs):
    return [ind for ind in range(freqs.size) if (1 <= freqs[ind] <= 4)]


def get_ind_by_tetha(freqs):
    return [ind for ind in range(freqs.size) if (4 <= freqs[ind] <= 8)]


def form_values_by_waves(ch_name, wave, freqs, fft_array, result_waves_structure, string_structure, current_chocolate, Check_array):
    av = 0
    if wave == 'alpha':
        av = fft_array[get_ind_by_alpha(freqs)]
    elif wave == 'beta':
        av = fft_array[get_ind_by_beta(freqs)]
    elif wave == 'gamma':
        av = fft_array[get_ind_by_gamma(freqs)]
    elif wave == 'delta':
        av = fft_array[get_ind_by_delta(freqs)]
    else:
        av = fft_array[get_ind_by_tetha(freqs)]
    if len(av) == 0:
        average = 0
    else:
        average = np.average(av)
    # проверяем в первый или во второй раз мы видим этот шоколад
    if Check_array[string_structure[current_chocolate][0]] == 'NA':
        result_waves_structure[ch_name][string_structure[current_chocolate][0]] = average
    else:
        result_waves_structure[ch_name][string_structure[current_chocolate][1]] = average


"""
ЗДЕСЬ БУДЕМ ФОРМИРОВАТЬ ФАЙЛЫ С ИНФОРМАЦИЕЙ о мощности сигнала 
-для заданного электрода
-в определенном частотном диапазоне
-при выбранной длине окна
-для заданного временного отступа
Сh_Fp1_alpha_window_2sec_delay_100ms.csv
"""


def transform_eeg_files(vol_names, channels_dict, wave, path_to_database='', window_s=2, delay_s=0.1, freq=500):
    window = window_s * freq
    delay = delay_s * freq
    channels = list(channels_dict.keys())
    channel_names = list(channels_dict.values())

    string_structure = {
        "AG": [0, 1],
        "Lindt": [2, 3],
        "Kremlin": [4, 5],
        "Fructose": [6, 7],
        "Carob": [8, 9]
    }

    first_line = "AG1; AG2; L1; L2; Kremlin1; Kremlin2; FR1; FR2; Carob1; Carob2\n"

    vols_structs = {}

    for vol_name in vol_names:

        path_to_volunteer_data = combine_path(path_to_database, vol_name)

        for ch in channel_names:
            text_file = open(path_to_volunteer_data + "/Ch_%s_window_%fsec_delay_%fms.csv" % (ch, window_s, delay_s),
                             "w+")
            text_file.write(first_line)
            text_file.close()

        Check_array = ['NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA', 'NA']
        result_waves_structure = initialize_wave_structure(channel_names)

        data_raw = my_import(path_to_volunteer_data, freq, 21, 0)

        # Инфа о порядке шоколада
        Lines_about_order = get_choco_order(path_to_volunteer_data)
        # Инфа о временных отсечках, т.е. о том, когда ели каждый образец шоколада
        times = list(map(float,
                         "".join(open(path_to_volunteer_data + '/times.txt', 'r')
                                 .readlines())
                         .split('\n')))[:10]
        i = 0

        for line in Lines_about_order:
            time = times[i]
            time_to_start = min(int(delay + time * freq), len(data_raw[0]) - 1)
            if i < 9:
                limit = int(times[i + 1] * freq)
            else:
                limit = len(data_raw[0])
            i += 1
            time_to_finish = min(int(time_to_start + window), limit)
            data_to_analyze = data_raw[:, time_to_start:time_to_finish]

            current_chocolate = line.strip()

            for ch_ind in channels:
                sigma_start = 5 * freq
                sigma_finish = 10 * freq
                data_to_sigma = data_raw[ch_ind, sigma_start: sigma_finish]
                sigma = stdev(data_to_sigma)
                pre_data = data_to_analyze[ch_ind, :]
                mean = np.mean(pre_data)
                data_to_fft = np.array([x for x in pre_data if mean - 3 * sigma <= x <= mean + 3 * sigma])
                if len(data_to_fft) > 0:
                    fft_array, freqs = my_fft(data_to_fft, freq)
                else:
                    fft_array = np.array([])
                    freqs = np.array([])
                form_values_by_waves(channels_dict[ch_ind], wave, freqs, fft_array, result_waves_structure,
                                     string_structure, current_chocolate, Check_array)
            Check_array[string_structure[current_chocolate][0]] = 'Done'

        for ch in channel_names:
            text_file = open(path_to_volunteer_data + "/Ch_%s_window_%fsec_delay_%fms.csv" % (ch, window_s, delay_s),
                             "a")
            line_to_save = ";".join(map(str, result_waves_structure[ch])) + '\n'
            text_file.write(line_to_save)
            text_file.close()

        vols_structs[vol_name] = result_waves_structure

    return vols_structs


def read_taste_for_vol(path_to_volunteer_data):
    text_file = open(path_to_volunteer_data + "/taste.csv", "r")
    return list(map(int, text_file.readlines()[-1].replace('\n', '').split(";")))


def read_price_for_vol(path_to_volunteer_data):
    text_file = open(path_to_volunteer_data + "/price.csv", "r")
    return list(map(int, text_file.readlines()[-1].replace('\n', '').split(";")))


def read_wtp_for_vol(path_to_volunteer_data):
    text_file = open(path_to_volunteer_data + "/WTP.csv", "r")
    return list(map(int, text_file.readlines()[-1].replace('\n', '').split(";")))


def form_dfs(vols_structs, vol_names, path_to_database, channel_dict):
    dfs = {}
    ch_names = list(channel_dict.values())
    for vol_name in vol_names:
        path_to_volunteer_data = combine_path(path_to_database, vol_name)
        lines_about_order = get_choco_order(path_to_volunteer_data)
        df = pd.DataFrame(columns=ch_names)
        for ch_name in ch_names:
            eeg = vols_structs[vol_name][ch_name]
            df[ch_name] = eeg
            df.index = lines_about_order
        df['taste'] = read_taste_for_vol(path_to_volunteer_data)
        df['price'] = read_price_for_vol(path_to_volunteer_data)
        df['wtp'] = read_wtp_for_vol(path_to_volunteer_data)
        dfs[vol_name] = df
    return dfs


def get_ch_type(electrode):
    return re.sub(r'[^a-zA-Z]', '', electrode).lower()


def find_symmetric_electrodes(channels):
    numbers = list(channels.keys())
    names = list(channels.values())
    groups = {}
    for name in names:
        e_type = get_ch_type(name)
        n_index = names.index(name)
        number = numbers[n_index]
        if e_type not in groups.keys():
            groups[e_type] = []
        groups[e_type].append(channels[number])
    return list(groups.values())


def calculate_asymmetry(ch_powers, groups):
    asymmetries = {}
    for i in range(len(ch_powers)):
        for j in range(i + 1, len(ch_powers)):
            i_arr = np.array(ch_powers[i])
            j_arr = np.array(ch_powers[j])
            asymmetry = (i_arr - j_arr) / (i_arr + j_arr)
            asymmetries[groups[i] + "-" + groups[j]] = asymmetry
    return asymmetries


def get_columns_of_group(struct, vol_name, group):
    return [struct[vol_name][k] for k in group]


def form_dfs_with_asymmetry(vols_structs, vol_names, path_to_database, channel_dict):
    dfs = {}
    for vol_name in vol_names:
        path_to_volunteer_data = combine_path(path_to_database, vol_name)
        lines_about_order = get_choco_order(path_to_volunteer_data)
        df = pd.DataFrame()
        groups = find_symmetric_electrodes(channel_dict)
        for group in groups:
            powers = get_columns_of_group(vols_structs, vol_name, group)
            asymmetries = calculate_asymmetry(powers, group)
            for a_key in asymmetries.keys():
                df[a_key] = asymmetries[a_key]
        df.index = lines_about_order
        df['taste'] = read_taste_for_vol(path_to_volunteer_data)
        df['price'] = read_price_for_vol(path_to_volunteer_data)
        df['wtp'] = read_wtp_for_vol(path_to_volunteer_data)
        dfs[vol_name] = df
    return dfs


def get_pvalues(df, features):
    import statsmodels.api as sm
    X = df[features]
    y = df['wtp']
    est = sm.OLS(y, X)
    res = est.fit()
    return dict(res.pvalues)


def append_dfs(dfs):
    temp_df = pd.DataFrame()
    for df in dfs:
        temp_df = temp_df.append(df)
    return temp_df


def get_pvalues_from_dfs(dfs, chosen_vols, chosen_features):
    chosen_dfs = [dfs[key] for key in chosen_vols]
    merged_df = append_dfs(chosen_dfs)
    return get_pvalues(merged_df, chosen_features)


def get_significant_values(res):
    return [key for (key, value) in res.items() if value <= 0.05]
