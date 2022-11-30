import os.path
import json
from flask import Flask, request, Response
import uuid
import pandas as pd
import numpy as np
from scipy import sparse
from sklearn.preprocessing import normalize

interactions_df = pd.read_csv("lastfm_user_scrobbles.csv")
titles_df = pd.read_csv("lastfm_artist_list.csv")

# Имена артистов
titles_df.index = titles_df["artist_id"]
title_dict = titles_df["artist_name"].to_dict()

#Получаем индексы в row, в r_pod индексы users
rows, r_pos = np.unique(interactions_df.values[:,0], return_inverse=True)
cols, c_pos = np.unique(interactions_df.values[:,1], return_inverse=True)

#Делаем sparse матрицу
interactions_sparse = sparse.csr_matrix((interactions_df.values[:,2], (r_pos, c_pos)) )

#Нормализуем и получаем матрицу похожести(l2 - норма). Получаем матрицу user - item.
Pui = normalize(interactions_sparse, norm='l2', axis=1)
sim = Pui.T * Pui

def rec_artist(id): # id - id артиста для рекомендаций
    #рекомендованные артисты для чувака который слушает артиста id
    return [title_dict[i+1] for i in sim[id].toarray().argsort()[0][-20:]]

interactions_sparse_transposed = interactions_sparse.transpose(copy=True)
Piu = normalize(interactions_sparse_transposed, norm='l2', axis=1)
#табличка рекомендаций для пользователей
fit = Pui * Piu * Pui

def rec_artist_for_user(user_id):#user_id - искомый пользователь
    # что слушал
    init_set = set([title_dict[i+1] for i in np.nonzero(interactions_sparse[user_id])[1].tolist()])
    # что рекомендуем
    pred_set = set([title_dict[i+1] for i in fit[user_id].toarray().argsort()[0][-70:].tolist()])
    return pred_set - init_set


def get_key(val):
    for key, value in title_dict.items():
        if val == value:
            return key

    return "key doesn't exist"

app = Flask(__name__)


@app.route("/")
def showHomePage():
    return "This is home page"


@app.route("/rec", methods=["POST"])
def rec():
    text = request.form["sample"]
    res = rec_artist(get_key(text))
    res = str(res)
    return res

app.run(host="0.0.0.0", port=8080)