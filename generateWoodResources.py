#!/bin/env python3
"""
This file generates a whole list of resources for the TerraFirmaCraft mod.
Any resource files generated by this script should set a root JSON tag:
    "__comment": "Generated by generateResources.py function: model"

You should set this script up to run automatically whenever you launch the game, and make sure it's run before you commit.
For IntelliJ instructions, see README.md.
"""

import json
import os
import time
import zipfile


def zipfolder(zip_name, target_dir):
    zipobj = zipfile.ZipFile(zip_name, 'w', zipfile.ZIP_DEFLATED)
    rootlen = len(target_dir) + 1
    for base, dirs, files in os.walk(target_dir):
        for file in files:
            fn = os.path.join(base, file)
            zipobj.write(fn, fn[rootlen:])


if not os.path.isdir('assets_backups'):
    os.mkdir('assets_backups')
    with open('assets_backups/.gitignore', 'w') as f:
        print(
            '# This folder does not belong on git. Not even as an empty folder, so we ignore everything, incl. this file.',
            file=f)
        print('*', file=f)

#zipfolder('assets_backups/{}.zip'.format(int(time.time())), 'src/main/resources/assets/tfc')

os.chdir('src/main/resources/assets/tfc/')

WOOD_TYPES = [
    'african_padauk',
    'alder',
    'angelim',
    'baobab',
    'beech',
    'black_walnut',
    'box',
    'brazilwood',
    'butternut',
    'cinnamon',
    'citrus',
    'cocobolo',
    'cypress',
    'ebony',
    'elder',
    'eucalyptus',
    'european_oak',
    'fever',
    'fir',
    'fruitwood',
    'giganteum',
    'ginkgo',
    'greenheart',
    'hawthorn',
    'hazel',
    'hemlock',
    'holly',
    'hornbeam',
    'ipe',
    'iroko',
    'ironwood',
    'jacaranda',
    'juniper',
    'kauri',
    'larch',
    'limba',
    'lime',
    'locust',
    'logwood',
    'maclura',
    'mahoe',
    'mahogany',
    'marblewood',
    'messmate',
    'mountain_ash',
    'nordmann_fir',
    'norway_spruce',
    'papaya',
    'persimmon',
    'pink_cherry',
    'pink_ivory',
    'plum',
    'poplar',
    'purpleheart',
    'red_cedar',
    'red_elm',
    'redwood',
    'rowan',
    'rubber_fig',
    'sweetgum',
    'syzygium',
    'teak',
    'wenge',
    'white_cherry',
    'white_elm',
    'whitebeam',
    'yellow_meranti',
    'yew',
    'zebrawood',
    'arrow_bamboo',
    'black_bamboo',
    'blue_bamboo',
    'dragon_bamboo',
    'golden_bamboo',
    'narrow_leaf_bamboo',
    'red_bamboo',
    'temple_bamboo',
    'thorny_bamboo',
    'timber_bamboo',
    'tinwa_bamboo',
    'weavers_bamboo',
]

# Special 'hardcoded' cases
DOOR_VARIANTS = {
    'normal': None,
    'facing=east,half=lower,hinge=left,open=false': {'model': 'door_bottom'},
    'facing=south,half=lower,hinge=left,open=false': {'model': 'door_bottom', 'y': 90},
    'facing=west,half=lower,hinge=left,open=false': {'model': 'door_bottom', 'y': 180},
    'facing=north,half=lower,hinge=left,open=false': {'model': 'door_bottom', 'y': 270},
    'facing=east,half=lower,hinge=right,open=false': {'model': 'door_bottom_rh'},
    'facing=south,half=lower,hinge=right,open=false': {'model': 'door_bottom_rh', 'y': 90},
    'facing=west,half=lower,hinge=right,open=false': {'model': 'door_bottom_rh', 'y': 180},
    'facing=north,half=lower,hinge=right,open=false': {'model': 'door_bottom_rh', 'y': 270},
    'facing=east,half=lower,hinge=left,open=true': {'model': 'door_bottom_rh', 'y': 90},
    'facing=south,half=lower,hinge=left,open=true': {'model': 'door_bottom_rh', 'y': 180},
    'facing=west,half=lower,hinge=left,open=true': {'model': 'door_bottom_rh', 'y': 270},
    'facing=north,half=lower,hinge=left,open=true': {'model': 'door_bottom_rh'},
    'facing=east,half=lower,hinge=right,open=true': {'model': 'door_bottom', 'y': 270},
    'facing=south,half=lower,hinge=right,open=true': {'model': 'door_bottom'},
    'facing=west,half=lower,hinge=right,open=true': {'model': 'door_bottom', 'y': 90},
    'facing=north,half=lower,hinge=right,open=true': {'model': 'door_bottom', 'y': 180},
    'facing=east,half=upper,hinge=left,open=false': {'model': 'tfc:door_top_tfc'},
    'facing=south,half=upper,hinge=left,open=false': {'model': 'tfc:door_top_tfc', 'y': 90},
    'facing=west,half=upper,hinge=left,open=false': {'model': 'tfc:door_top_tfc', 'y': 180},
    'facing=north,half=upper,hinge=left,open=false': {'model': 'tfc:door_top_tfc', 'y': 270},
    'facing=east,half=upper,hinge=right,open=false': {'model': 'tfc:door_top_rh_tfc'},
    'facing=south,half=upper,hinge=right,open=false': {'model': 'tfc:door_top_rh_tfc', 'y': 90},
    'facing=west,half=upper,hinge=right,open=false': {'model': 'tfc:door_top_rh_tfc', 'y': 180},
    'facing=north,half=upper,hinge=right,open=false': {'model': 'tfc:door_top_rh_tfc', 'y': 270},
    'facing=east,half=upper,hinge=left,open=true': {'model': 'tfc:door_top_rh_tfc', 'y': 90},
    'facing=south,half=upper,hinge=left,open=true': {'model': 'tfc:door_top_rh_tfc', 'y': 180},
    'facing=west,half=upper,hinge=left,open=true': {'model': 'tfc:door_top_rh_tfc', 'y': 270},
    'facing=north,half=upper,hinge=left,open=true': {'model': 'tfc:door_top_rh_tfc'},
    'facing=east,half=upper,hinge=right,open=true': {'model': 'tfc:door_top_tfc', 'y': 270},
    'facing=south,half=upper,hinge=right,open=true': {'model': 'tfc:door_top_tfc'},
    'facing=west,half=upper,hinge=right,open=true': {'model': 'tfc:door_top_tfc', 'y': 90},
    'facing=north,half=upper,hinge=right,open=true': {'model': 'tfc:door_top_tfc', 'y': 180}
}
TRAPDOOR_VARIANTS = {
    'normal': None,
    'facing=north,half=bottom,open=false': {'model': 'tfc:trapdoor_tfc', 'x': 180},
    'facing=south,half=bottom,open=false': {'model': 'tfc:trapdoor_tfc', 'x': 180},
    'facing=east,half=bottom,open=false': {'model': 'tfc:trapdoor_tfc', 'x': 180},
    'facing=west,half=bottom,open=false': {'model': 'tfc:trapdoor_tfc', 'x': 180},
    'facing=north,half=top,open=false': {'model': 'tfc:trapdoor_tfc'},
    'facing=south,half=top,open=false': {'model': 'tfc:trapdoor_tfc'},
    'facing=east,half=top,open=false': {'model': 'tfc:trapdoor_tfc'},
    'facing=west,half=top,open=false': {'model': 'tfc:trapdoor_tfc'},
    'facing=north,half=bottom,open=true': {'model': 'tfc:trapdoor_tfc', 'x': 270},
    'facing=south,half=bottom,open=true': {'model': 'tfc:trapdoor_tfc', 'x': 90},
    'facing=east,half=bottom,open=true': {'model': 'tfc:trapdoor_tfc', 'x': 270, 'y': 90},
    'facing=west,half=bottom,open=true': {'model': 'tfc:trapdoor_tfc', 'x': 270, 'y': 270},
    'facing=north,half=top,open=true': {'model': 'tfc:trapdoor_tfc', 'x': 270},
    'facing=south,half=top,open=true': {'model': 'tfc:trapdoor_tfc', 'x': 90},
    'facing=east,half=top,open=true': {'model': 'tfc:trapdoor_tfc', 'x': 270, 'y': 90},
    'facing=west,half=top,open=true': {'model': 'tfc:trapdoor_tfc', 'x': 270, 'y': 270}
}
STAIR_VARIANTS = {
    'normal': {'model': 'stairs'},
    'facing=east,half=bottom,shape=straight': {'model': 'stairs'},
    'facing=west,half=bottom,shape=straight': {'model': 'stairs', 'y': 180},
    'facing=south,half=bottom,shape=straight': {'model': 'stairs', 'y': 90},
    'facing=north,half=bottom,shape=straight': {'model': 'stairs', 'y': 270},
    'facing=east,half=bottom,shape=outer_right': {'model': 'outer_stairs'},
    'facing=west,half=bottom,shape=outer_right': {'model': 'outer_stairs', 'y': 180},
    'facing=south,half=bottom,shape=outer_right': {'model': 'outer_stairs', 'y': 90},
    'facing=north,half=bottom,shape=outer_right': {'model': 'outer_stairs', 'y': 270},
    'facing=east,half=bottom,shape=outer_left': {'model': 'outer_stairs', 'y': 270},
    'facing=west,half=bottom,shape=outer_left': {'model': 'outer_stairs', 'y': 90},
    'facing=south,half=bottom,shape=outer_left': {'model': 'outer_stairs'},
    'facing=north,half=bottom,shape=outer_left': {'model': 'outer_stairs', 'y': 180},
    'facing=east,half=bottom,shape=inner_right': {'model': 'inner_stairs'},
    'facing=west,half=bottom,shape=inner_right': {'model': 'inner_stairs', 'y': 180},
    'facing=south,half=bottom,shape=inner_right': {'model': 'inner_stairs', 'y': 90},
    'facing=north,half=bottom,shape=inner_right': {'model': 'inner_stairs', 'y': 270},
    'facing=east,half=bottom,shape=inner_left': {'model': 'inner_stairs', 'y': 270},
    'facing=west,half=bottom,shape=inner_left': {'model': 'inner_stairs', 'y': 90},
    'facing=south,half=bottom,shape=inner_left': {'model': 'inner_stairs'},
    'facing=north,half=bottom,shape=inner_left': {'model': 'inner_stairs', 'y': 180},
    'facing=east,half=top,shape=straight': {'model': 'stairs', 'x': 180},
    'facing=west,half=top,shape=straight': {'model': 'stairs', 'x': 180, 'y': 180},
    'facing=south,half=top,shape=straight': {'model': 'stairs', 'x': 180, 'y': 90},
    'facing=north,half=top,shape=straight': {'model': 'stairs', 'x': 180, 'y': 270},
    'facing=east,half=top,shape=outer_right': {'model': 'outer_stairs', 'x': 180, 'y': 90},
    'facing=west,half=top,shape=outer_right': {'model': 'outer_stairs', 'x': 180, 'y': 270},
    'facing=south,half=top,shape=outer_right': {'model': 'outer_stairs', 'x': 180, 'y': 180},
    'facing=north,half=top,shape=outer_right': {'model': 'outer_stairs', 'x': 180},
    'facing=east,half=top,shape=outer_left': {'model': 'outer_stairs', 'x': 180},
    'facing=west,half=top,shape=outer_left': {'model': 'outer_stairs', 'x': 180, 'y': 180},
    'facing=south,half=top,shape=outer_left': {'model': 'outer_stairs', 'x': 180, 'y': 90},
    'facing=north,half=top,shape=outer_left': {'model': 'outer_stairs', 'x': 180, 'y': 270},
    'facing=east,half=top,shape=inner_right': {'model': 'inner_stairs', 'x': 180, 'y': 90},
    'facing=west,half=top,shape=inner_right': {'model': 'inner_stairs', 'x': 180, 'y': 270},
    'facing=south,half=top,shape=inner_right': {'model': 'inner_stairs', 'x': 180, 'y': 180},
    'facing=north,half=top,shape=inner_right': {'model': 'inner_stairs', 'x': 180},
    'facing=east,half=top,shape=inner_left': {'model': 'inner_stairs', 'x': 180},
    'facing=west,half=top,shape=inner_left': {'model': 'inner_stairs', 'x': 180, 'y': 180},
    'facing=south,half=top,shape=inner_left': {'model': 'inner_stairs', 'x': 180, 'y': 90},
    'facing=north,half=top,shape=inner_left': {'model': 'inner_stairs', 'x': 180, 'y': 270}
}
FENCE_GATE_VARIANTS = {
    'facing=south,in_wall=false,open=false': {'model': 'fence_gate_closed'},
    'facing=west,in_wall=false,open=false': {'model': 'fence_gate_closed', 'y': 90},
    'facing=north,in_wall=false,open=false': {'model': 'fence_gate_closed', 'y': 180},
    'facing=east,in_wall=false,open=false': {'model': 'fence_gate_closed', 'y': 270},
    'facing=south,in_wall=false,open=true': {'model': 'fence_gate_open'},
    'facing=west,in_wall=false,open=true': {'model': 'fence_gate_open', 'y': 90},
    'facing=north,in_wall=false,open=true': {'model': 'fence_gate_open', 'y': 180},
    'facing=east,in_wall=false,open=true': {'model': 'fence_gate_open', 'y': 270},
    'facing=south,in_wall=true,open=false': {'model': 'wall_gate_closed'},
    'facing=west,in_wall=true,open=false': {'model': 'wall_gate_closed', 'y': 90},
    'facing=north,in_wall=true,open=false': {'model': 'wall_gate_closed', 'y': 180},
    'facing=east,in_wall=true,open=false': {'model': 'wall_gate_closed', 'y': 270},
    'facing=south,in_wall=true,open=true': {'model': 'wall_gate_open'},
    'facing=west,in_wall=true,open=true': {'model': 'wall_gate_open', 'y': 90},
    'facing=north,in_wall=true,open=true': {'model': 'wall_gate_open', 'y': 180},
    'facing=east,in_wall=true,open=true': {'model': 'wall_gate_open', 'y': 270}
}


def del_none(d):
    """
    https://stackoverflow.com/a/4256027/4355781
    Modifies input!
    """
    for key, value in list(d.items()):
        if value is None:
            del d[key]
        elif isinstance(value, dict):
            del_none(value)
    return d


def blockstate(filename_parts, model, textures, variants=None, uvlock=None):
    """
    Magic.
    :param filename_parts: Iterable of strings.
    :param model: String or None
    :param textures: Dict of <string>:<string> OR <iterable of strings>:<string>
    :param variants: Dict of <string>:<variant> OR "normal":None (to disable the normal default)
    """
    _variants = {
        'normal': [{}]
    }
    if variants:
        _variants.update(variants)

    # Unpack any tuple keys to simple string->string pairs
    _textures = {}
    for key, val in textures.items():
        if isinstance(key, str):
            _textures[key] = val
        else:
            for x in key:
                _textures[x] = val

    p = os.path.join('blockstates', *filename_parts) + '.json'
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w') as file:
        json.dump(del_none({
            '__comment': 'Generated by generateResources.py function: blockstate',
            'forge_marker': 1,
            'defaults': {
                'model': model,
                'textures': _textures,
                'uvlock': True if uvlock else None
            },
            'variants': _variants
        }), file, indent=2)


def cube_all(filename_parts, texture, variants=None, model='cube_all'):
    blockstate(filename_parts, model, textures={'all': texture}, variants=variants)


def model(filename_parts, parent, textures):
    p = os.path.join('models', *filename_parts) + '.json'
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w') as file:
        json.dump(del_none({
            '__comment': 'Generated by generateResources.py function: model',
            'parent': parent,
            'textures': textures,
        }), file, indent=2)


def item(filename_parts, *layers, parent='item/generated'):
    model(('item', *filename_parts), parent,
          None if len(layers) == 0 else {'layer%d' % i: v for i, v in enumerate(layers)})

#   ____  _            _        _        _
#  |  _ \| |          | |      | |      | |
#  | |_) | | ___   ___| | _____| |_ __ _| |_ ___  ___
#  |  _ <| |/ _ \ / __| |/ / __| __/ _` | __/ _ \/ __|
#  | |_) | | (_) | (__|   <\__ \ || (_| | ||  __/\__ \
#  |____/|_|\___/ \___|_|\_\___/\__\__,_|\__\___||___/
#
# BLOCKSTATES

# WOOD STUFF
for wood_type in WOOD_TYPES:
    # LOG BLOCKS
    blockstate(('wood', 'log', wood_type), 'item/generated', textures={
        ('particle', 'side'): 'tfc:blocks/wood/log/%s' % wood_type,
        'end': 'tfc:blocks/wood/top/%s' % wood_type,
        'layer0': 'tfc:items/wood/log/%s' % wood_type,
    }, variants={
        'axis': {
            'y': {'model': 'cube_column'},
            'z': {'model': 'cube_column', 'x': 90},
            'x': {'model': 'cube_column', 'x': 90, 'y': 90},
            'none': {
                'model': 'cube_column',
                'textures': {'end': 'tfc:blocks/wood/log/%s' % wood_type}
            }
        },
        'small': {
            'true': {'model': 'tfc:small_log'},
            'false': {},
        }
    })

    # PLANKS BLOCKS
    cube_all(('wood', 'planks', wood_type), 'tfc:blocks/wood/planks/%s' % wood_type)
    # LEAVES BLOCKS
    if wood_type != 'palm':
        cube_all(('wood', 'leaves', wood_type), 'tfc:blocks/wood/leaves/%s' % wood_type, model='leaves')

    # FENCES
    blockstate(('wood', 'fence', wood_type), 'fence_post', textures={
        'texture': 'tfc:blocks/wood/planks/%s' % wood_type
    }, variants={
        'inventory': {'model': 'fence_inventory'},
        'north': {'true': {'submodel': 'fence_side'}, 'false': {}},
        'east': {'true': {'submodel': 'fence_side', 'y': 90}, 'false': {}},
        'south': {'true': {'submodel': 'fence_side', 'y': 180}, 'false': {}},
        'west': {'true': {'submodel': 'fence_side', 'y': 270}, 'false': {}},
    })

    # SAPLINGS
    blockstate(('wood', 'sapling', wood_type), 'cross', textures={
        ('cross', 'layer0'): 'tfc:blocks/saplings/%s' % wood_type
    }, variants={
        'inventory': {
            'model': 'builtin/generated',
            'transform': 'forge:default-item'
        }
    })

    # DOORS
    blockstate(('wood', 'door', wood_type), None, textures={
        'bottom': 'tfc:blocks/wood/door/lower/%s' % wood_type,
        'top': 'tfc:blocks/wood/door/upper/%s' % wood_type,
    }, variants=DOOR_VARIANTS)

    # TOOL RACKS
    blockstate(('wood', 'tool_rack', wood_type), 'tfc:tool_rack', textures={
        'texture': 'tfc:blocks/wood/planks/%s' % wood_type,
        'particle': 'tfc:blocks/wood/planks/%s' % wood_type,
    }, variants={
        'facing': {
            'south': {},
            'west': {'y': 90},
            'north': {'y': 180},
            'east': {'y': 270},
        }
    })

    # (WOOD) STAIRS & SLABS
    blockstate(('stairs', 'wood', wood_type), None, textures={
        ('top', 'bottom', 'side'): 'tfc:blocks/wood/planks/%s' % wood_type,
    }, variants=STAIR_VARIANTS, uvlock=True)
    blockstate(('slab', 'wood', wood_type), 'half_slab', textures={
        ('top', 'bottom', 'side'): 'tfc:blocks/wood/planks/%s' % wood_type,
    }, variants={
        'half': {
            'bottom': {},
            'top': {'model': 'upper_slab'}
        }
    })
    cube_all(('double_slab', 'wood', wood_type), 'tfc:blocks/wood/planks/%s' % wood_type)

    # (WOOD) TRAPDOORS
    blockstate(('wood', 'trapdoor', wood_type), None, textures={
        'texture': 'tfc:blocks/wood/trapdoor/%s' % wood_type
    }, variants=TRAPDOOR_VARIANTS)

    # FenceGates
    blockstate(('wood', 'fence_gate', wood_type), None, textures={
        'texture': 'tfc:blocks/wood/planks/%s' % wood_type
    }, variants=FENCE_GATE_VARIANTS, uvlock=True)

    # CHESTS
    blockstate(('wood', 'chest', wood_type), 'tfc:chest', textures={
        'texture': 'tfc:entity/chests/chest/%s' % wood_type,
        'particle': 'tfc:entity/chests/chest/%s' % wood_type,
    })
    blockstate(('wood', 'chest_trap', wood_type), 'tfc:chest', textures={
        'texture': 'tfc:entity/chests/chest_trap/%s' % wood_type,
        'particle': 'tfc:entity/chests/chest_trap/%s' % wood_type,
    })

    # (WOOD) BUTTON
    blockstate(('wood', 'button', wood_type), 'button', textures={
        ('texture', 'particle'): 'tfc:blocks/wood/planks/%s' % wood_type,
    }, variants={
        'powered': {
            'false': {},
            'true': {'model': 'button_pressed'}
        },
        'facing': {
            'up': {},
            'down': {'x': 180},
            'east': {'x': 90, 'y': 90},
            'west': {'x': 90, 'y': 270},
            'south': {'x': 90, 'y': 180},
            'north': {'x': 90},
        },
        'inventory': [{
            'model': 'button_inventory'
        }]
    })

    # BOOKSHELF
    blockstate(('wood', 'bookshelf', wood_type), 'tfc:bookshelf', textures={
        ('all', 'particle'): 'tfc:blocks/wood/planks/%s' % wood_type,
        ('north', 'south', 'east', 'west'): 'tfc:blocks/wood/bookshelf',
    })

    # WORKBENCH
    blockstate(('wood', 'workbench', wood_type), 'tfc:workbench', textures={
        ('all', 'particle'): 'tfc:blocks/wood/planks/%s' % wood_type,
        'top': 'tfc:blocks/wood/workbench_top',
        ('north', 'south'): 'tfc:blocks/wood/workbench_front',
        ('east', 'west'): 'tfc:blocks/wood/workbench_side',
    })

    # BARREL
    blockstate(('wood', 'barrel', wood_type), 'tfc:barrel', textures={
        ('particle', 'planks'): 'tfc:blocks/wood/planks/%s' % wood_type,
        'sheet': 'tfc:blocks/wood/sheets/%s' % wood_type,
        'hoop': 'tfc:blocks/barrelhoop',
    }, variants={
        'sealed': {
            'true': {'model': 'tfc:barrel_sealed'},
            'false': {},
        },
        'inventory': [{}]
    })

    # LOOM
    blockstate(('wood', 'loom', wood_type), 'tfc:loom', textures={
        'texture': 'tfc:blocks/wood/planks/%s' % wood_type,
        'particle': 'tfc:blocks/wood/planks/%s' % wood_type,
    }, variants={
        'facing': {
            'south': {},
            'west': {'y': 90},
            'north': {'y': 180},
            'east': {'y': 270},
        }
    })

    # SUPPORT
    blockstate(('wood', 'support', wood_type), 'tfc:support/vertical', textures={
        'texture': 'tfc:blocks/wood/sheets/%s' % wood_type,
        'particle': 'tfc:blocks/wood/sheets/%s' % wood_type,
    }, variants={
        'inventory': {'model': 'tfc:support/inventory'},
        'axis': {
            'y': {'model': 'tfc:support/vertical'}, 'x': {'model': 'tfc:support/horizontal'},
            'z': {'model': 'tfc:support/horizontal', 'y': 90}
        },
        'north': {'true': {'submodel': 'tfc:support/connection', 'y': 270}, 'false': {}},
        'east': {'true': {'submodel': 'tfc:support/connection'}, 'false': {}},
        'south': {'true': {'submodel': 'tfc:support/connection', 'y': 90}, 'false': {}},
        'west': {'true': {'submodel': 'tfc:support/connection', 'y': 180}, 'false': {}},
    })

#   _____ _
#  |_   _| |
#    | | | |_ ___ _ __ ___  ___
#    | | | __/ _ \ '_ ` _ \/ __|
#   _| |_| ||  __/ | | | | \__ \
#  |_____|\__\___|_| |_| |_|___/
# 
# ITEMS

# DOORS / TRAPDOORS /FENCE_GATE
for wood_type in WOOD_TYPES:
    item(('wood', 'log', wood_type), 'tfc:items/wood/log/%s' % wood_type)
    item(('wood', 'door', wood_type), 'tfc:items/wood/door/%s' % wood_type)

    # Trapdoors are special - their item model needs to reference the blockstate #texture
    model(('item', 'wood', 'trapdoor', wood_type), 'block/trapdoor_bottom',
          {'texture': 'tfc:blocks/wood/trapdoor/%s' % wood_type})
    model(('item', 'wood', 'fence_gate', wood_type), 'block/fence_gate_closed',
          {'texture': 'tfc:blocks/wood/planks/%s' % wood_type})

model(('item','stick_bunch'), 'item/generated', {'layer0': 'tfc:items/stick_bunch'})

# WOOD STUFF
for wood_type in WOOD_TYPES:
    item(('wood', 'lumber', wood_type), 'tfc:items/wood/lumber/%s' % wood_type)
    item(('wood', 'boat', wood_type), 'tfc:items/wood/boat/%s' % wood_type)
