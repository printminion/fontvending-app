
import os

from compiler.ast import TryExcept

data = {'license: ': None,
        'url: ': None,
        'category: ': None,
        'subsets: ': None,
        'family: ': None,
        'group: ': None,
        
        'designer: ': '/designer/name',
        'profile: ': '/designer/description',
        
        'profiledescriptionlicense: ': None,
        'profiledescriptionlicenseurl: ': None,
        'originaldesigner: ': None,
        'approved: ': None,
        'visibility: ': None,
        'description: ': None
        }

known_subsets = ('latin', 'cyrillic', 'khmer', 'arabic', 'greek', 'hebrew', 'korean', 'vietnamese')

def parceFontMetadata(path):

    try:
        f = open(path + '\\METADATA', 'r') # open -- file may get suffix added by low-level library
    except ZeroDivisionError:
        print "Attempted to divide by zero"
    
    
                          
    lines = f.readlines()

    #profiledescriptionlicense:
    #profiledescriptionlicenseurl: 
    #approved:
    #originaldesigner: 


#    "font-family": "Allan",
#            "category": "normal",
#            "font-style": "normal",
#            "font-weight": "bold",
#            "format": "truetype",
#            "local-name": "Allan",
#            "local-file": "Allan",
#            "preview": "Allan",
#            "url": "http://themes.googleusercontent.com/font?kit=QNNVW1nta25A1TfGw7wquPesZW2xOQ-xsNqO47m55DA",
#            "md5": null,
#            "subsets": {
#                "cyrillic": true,
#                "greek": true,
#                "khmer": true,
#                "latin": true
#            },
#            "designer": "Anton Koovit",
#            "license": {
#                "name": null,
#                "url": null
#            }

#"designers": [
#        {
#            "name": "Steve Matteson",
#            "description": "Steve Matteson is the Director of Type Design for Ascender Corporation and has created hundreds of fonts for use in various screen display environments and print publishing since 1987. A graduate of the Rochester Institute of Technology, Steve has an extensive background in typography, design and printing which he has applied to his development of high quality typefaces. His work can be found in User Interface designs (such as Google's Android platform, Windows Vista and Xbox 360); in publishing (such as Andy, Bertham, Endurance Pro and Pescadero Pro); and for corporate branding (such as Alcon Labs, Microsoft and Symantec). He resides in Louisville, CO with his wife, 2 kids and 2 Labrador Retrievers.",
#            "image": "http://themes.googleusercontent.com/image?id=0BwVBOzw_-hbMM2I5Yjk1MWMtYmVlNi00ZTJmLTkyNjItNjU3MDExMTUxMTdl&th=150&tw=100",
#            "site": "http://www.ascendercorp.com"
#        }

    fontadata = {}
    fontadata['font'] = {'font-family': None,
                         'category': None,
                         'font-style': 'normal',
                         'font-weight': 'normal',
                         'format': 'truetype',
                         'local-name': None,
                         'local-file': None,
                         'preview': None,
                         'url': None,
                         'md5': None,
                         'subsets': {},
                         'designer': None
                         }
    
    fontadata['font']['license'] = {'name': None, 'url': None }
    fontadata['designer'] = {'name': None, 'description': None, 'image': None, 'site': None}
     
    
    visibility = True
    approved = True
    PARSING_START = False
    CURRENT_PREFIX = None
    
    for line in lines[:]:
        ''' 
            skip comments 
        '''
        prefix = '#'
        if line.startswith(prefix): 
            CURRENT_PREFIX = None
            PARSING_START = False
            continue
        
        ''' 
            check visivility 
        '''
        prefix = 'visibility: '
        if line.startswith(prefix): 
            if line.find('INTERNAL') > -1:
                visibility = False
            PARSING_START = False
            continue
        
        prefix = 'approved: '
        if line.startswith(prefix): 
            if stripType(prefix, line) != 'true':
                approved = False
            PARSING_START = False
            continue
        
        
        prefix = 'license: '
        if line.startswith(prefix): 
            license = stripType(prefix, line)
            if license == 'OFL':
                fontadata['font']['license']['name'] = 'SIL Open Font License, 1.1'
                fontadata['font']['license']['url'] = 'http://scripts.sil.org/OFL'
            else:
                fontadata['font']['license']['name'] = stripType(prefix, line)
            PARSING_START = False
            continue
        
        
        prefix = 'designer: '
        if line.startswith(prefix): 
            fontadata['designer']['name'] = stripType(prefix, line)
            fontadata['font']['designer'] = stripType(prefix, line)
            PARSING_START = False
            continue
        
        prefix = 'profile: '
        if line.startswith(prefix): 
            fontadata['designer']['description'] = stripType(prefix, line)
            continue
       
        prefix = 'description: '
        if line.startswith(prefix): 
            CURRENT_PREFIX = prefix
            description = stripType(prefix, line)
            fontadata['font']['description'] = stripLast(description)
            PARSING_START = True
            continue
        
        prefix = 'description.ru: '
        if line.startswith(prefix): 
            CURRENT_PREFIX = prefix
            PARSING_START = True
            continue
        
        prefix = 'family: '
        if line.startswith(prefix): 
            fontadata['font']['font-family'] = stripType(prefix, line)
            fontadata['font']['local-name'] = doLocalName(stripType(prefix, line))
            fontadata['font']['local-file'] = fontadata['font']['local-name']
            fontadata['font']['preview'] = stripType(prefix, line)
            PARSING_START = False
            continue

        prefix = 'font.'
        if line.startswith(prefix): 
            font = stripType(prefix, line)
            if fontadata['font'].has_key('names'):
                fontadata['font']['names'] += ', '
                fontadata['font']['names'] += font
            else:
                fontadata['font']['names'] = font
                
            PARSING_START = False
            continue

        prefix = 'category: '
        if line.startswith(prefix): 
            fontadata['font']['category'] = stripType(prefix, line)
            PARSING_START = False
            continue
         
        prefix = 'subsets: '
        if line.startswith(prefix): 
            fontadata['font']['subsets'] = parseSubsets(prefix, line)
            PARSING_START = False
            continue

        prefix = 'url: '
        if line.startswith(prefix): 
            fontadata['designer']['site'] = stripType(prefix, line)
            PARSING_START = False
            continue 
        
        
        '''
            check if this string is still description
        '''
        prefix = 'description: '
        if prefix == CURRENT_PREFIX and PARSING_START == True:
            line = stripLast(line)
            fontadata['font']['description'] += ' '
            fontadata['font']['description'] += line
            
            continue        
    #end for
        
    if (approved == False or visibility == False):
        err = fontadata['font']['local-name'],'\t','visible:',visibility,'\t','approved:',approved,'\t',path
        raise NameError(err) 
    
    ''' prevent no names '''
    if fontadata['font']['local-name'] == None:
        fontadata['font']['font-family'] = os.path.basename(path)
        fontadata['font']['local-name'] = fontadata['font']['font-family']
        fontadata['font']['local-file'] = fontadata['font']['font-family']
        fontadata['font']['preview'] = fontadata['font']['font-family']

        
    return fontadata


def parceDesignerMetadata(dirname, designer_data):
   
    designer_name = designer_data['name']
    designer_name = designer_name.replace(' ', '_')
    designer_name = designer_name.lower()
    
#    try:
    f = open(dirname + 'designers\\' + designer_name + '\\BIO', 'r') # open -- file may get suffix added by low-level library
#    except ZeroDivisionError:
#        print "Attempted to divide by zero"
    
    lines = f.readlines()

#name: Tart Workshop
#portrait: http://themes.googleusercontent.com/
#url: http://www.tartworkshop.com
#bio: <p>Lettering artist/illustrator Crystal Kl
    PARSING_START = False
    CURRENT_PREFIX = None
        
    for line in lines[:]:
        ''' 
            skip comments 
        '''
        prefix = '#'
        if line.startswith(prefix): 
            PARSING_START = False
            continue
        
        prefix = 'name: '
        if line.startswith(prefix): 
            value = stripType(prefix, line)
            designer_data['name'] = value
            PARSING_START = False
            continue

        prefix = 'portrait: '
        if line.startswith(prefix): 
            value = stripType(prefix, line)
            designer_data['image'] = value
            PARSING_START = False
            continue

        prefix = 'bio: '
        if line.startswith(prefix): 
            value = stripType(prefix, line)
            value = stripLast(value)
            designer_data['description'] = value
            CURRENT_PREFIX = prefix
            PARSING_START = True
            continue

        prefix = 'url: '
        if line.startswith(prefix): 
            value = stripType(prefix, line)
            designer_data['site'] = value
            continue
        
        '''
            check if this string is still description
        '''
        prefix = 'bio: '
        if prefix == CURRENT_PREFIX and PARSING_START == True:
            value = stripLast(line)
            designer_data['description'] += ' '
            designer_data['description'] += value
            
            continue                

    return designer_data


def doLocalName(line):
    
    return line.replace(' ', '')

def stripType(key, string):
    string = string.rstrip()
    
    return string.lstrip(key)

def stripLast(line):

    line = line.strip()
    if line.endswith('\\'):
        line = line.rstrip('\\')
    line = line.strip()            
                

    #if line.rfind( '\\' ):
    #print 'Found bad char on:',line.rfind( '\\' ),' : >',line,'<'
        
    return line


def parseSubsets(key, string):
    
    string = string.rstrip()
    string = string.lstrip(key)
    
    subsets = string.split(',')
    
    
    
    subset_list = []
    
    '''
        check if subset is known
    '''
    for subset in subsets[:]:
        
        if '+' in subset:
            subset_sublist = subset.split('+')
            print subset_sublist
            subset = subset_sublist[0]
            
        if subset in known_subsets:
            subset_list.insert(len(subset_list),subset)
    
    #print subsets,'->',subset_list
    
    return subset_list

