#include <gmodule.h>

typedef GHashTable* mapped_list_t;
typedef GSList*		list_t; 


void list_free(list_t list){
	return g_slist_free_full(list, free);
}

mapped_list_t mapped_list_new() {
	return g_hash_table_new_full(g_str_hash, g_str_equal, g_free, NULL);
}

int mapped_list_add(mapped_list_t map, char* key, void* val) {
	list_t l = NULL;
	if (g_hash_table_contains(map, key))
		l = g_hash_table_lookup(map, key);
	l = g_slist_prepend (l, val);
	return g_hash_table_insert(map, g_strdup(key), l);
}

list_t mapped_list_get(mapped_list_t map, char* key) {
	return g_hash_table_lookup(map, key);
}

list_t mapped_list_next(list_t list) {
	return g_slist_next(list);
}

void mapped_list_destroy(mapped_list_t map, char* key) {
	return g_hash_table_destroy(map);
}