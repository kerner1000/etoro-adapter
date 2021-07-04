delete ticker_taxonomies
from ticker_taxonomies
inner join taxonomies on ticker_taxonomies.taxonomies_id = taxonomies.id
where value = 'na';

delete taxonomies
from taxonomies
where value = 'na';