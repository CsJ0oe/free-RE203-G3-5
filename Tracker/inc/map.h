#include <gmodule.h>

typedef GHashTable* map_t;

map_t map_new() {
	// TODO : free key and val
	return g_hash_table_new_full(g_str_hash, g_str_equal, g_free, NULL);
}

int map_insert(map_t map, char* key, void* val) {
	return g_hash_table_insert(map, g_strdup(key), val);
}

void* map_lookup(map_t map, char* key) {
	return g_hash_table_lookup(map, key);
}

void map_destroy(map_t map, char* key) {
	return g_hash_table_destroy(map);
}