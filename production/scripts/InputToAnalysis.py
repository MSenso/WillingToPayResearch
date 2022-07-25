import sys

import ChocolateAnalysis as ca

path_to_database = sys.argv[1]
vol_names = eval(sys.argv[2])
ch = eval(sys.argv[3])
wave = sys.argv[4]
window = float(sys.argv[5])
delay = float(sys.argv[6])
analysisType = sys.argv[7]


ca.import_taste_data(vol_names, path_to_database)
vols_structs = ca.transform_eeg_files(vol_names, ch, wave,
                                      path_to_database=path_to_database,
                                      window_s=window,
                                      delay_s=delay,
                                      freq=500)
if analysisType == "power":
    dfs = ca.form_dfs(vols_structs, vol_names, path_to_database, ch)
    features = list(ch.values()) + ["taste", "price"]
else:
    dfs = ca.form_dfs_with_asymmetry(vols_structs, vol_names, path_to_database, ch)
    features = list(dfs[vol_names[0]].drop(['wtp'], axis=1).columns)

res = ca.get_pvalues_from_dfs(dfs, vol_names, features)
print(res)
print(ca.get_significant_values(res))
